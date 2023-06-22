package ru.aslastin

import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import ru.aslastin.component.CompanyComponent
import ru.aslastin.component.UserComponent
import ru.aslastin.dto.CompanyInfoDto
import ru.aslastin.dto.UserBalanceDto
import ru.aslastin.dto.UserOfferDto

@SpringBootTest
class IntegrationTest(
    @Autowired private val companyComponent: CompanyComponent,
    @Autowired private val userComponent: UserComponent
) {
    @JvmField
    @Rule
    final val exchangeApplication: GenericContainer<*> =
        GenericContainer(DockerImageName.parse("stock-exchange-application:latest"))
            .withExposedPorts(8080)

    private fun createCompany(companyId: Long, pricePerStock: Long): CompanyInfoDto {
        val company = companyComponent.createCompany(companyId, pricePerStock)
        assertEquals(companyId, company.companyId)
        assertEquals(pricePerStock, company.pricePerStock)
        assertEquals(0, company.countStocks)
        return company
    }

    private fun addStocks(company: CompanyInfoDto, countStocksToAdd: Int): CompanyInfoDto {
        val updatedCompany = companyComponent.addCompanyStocks(company.companyId, countStocksToAdd)
        assertEquals(company.companyId, updatedCompany.companyId)
        assertEquals(company.pricePerStock, updatedCompany.pricePerStock)
        assertEquals(company.countStocks + countStocksToAdd, updatedCompany.countStocks)
        return updatedCompany
    }

    private fun changePricePerStock(
        company: CompanyInfoDto,
        newPricePerStock: Long
    ): CompanyInfoDto {
        val updatedCompany = companyComponent.changeCompanyPricePerStock(
            company.companyId, newPricePerStock
        )
        assertEquals(company.companyId, updatedCompany.companyId)
        assertEquals(newPricePerStock, updatedCompany.pricePerStock)
        assertEquals(company.countStocks, updatedCompany.countStocks)
        return updatedCompany
    }

    private fun checkGetCompany(company: CompanyInfoDto) {
        assertEquals(company, companyComponent.getCompanyInfo(company.companyId))
    }

    private fun getUserTotalBalance(userId: Long): Long =
        userComponent.getUserTotalBalance(userId).totalBalance

    private fun getUserCompanyCountStocks(userId: Long, companyId: Long): Int? =
        userComponent.getUserStocks(userId).purchasedStocks.firstOrNull { it.companyId == companyId }?.countStocks

    private fun buyCompanyStocks(
        company: CompanyInfoDto,
        user: UserBalanceDto,
        countStocksToBuy: Int
    ): Pair<CompanyInfoDto, UserBalanceDto> {
        val userCountStocks = getUserCompanyCountStocks(user.userId, company.companyId) ?: 0

        companyComponent.buyStocks(company.companyId, user.userId, countStocksToBuy)

        val updatedCompany = companyComponent.getCompanyInfo(company.companyId)
        assertEquals(company.companyId, updatedCompany.companyId)
        assertEquals(company.countStocks - countStocksToBuy, updatedCompany.countStocks)
        assertEquals(company.pricePerStock, updatedCompany.pricePerStock)

        val updatedUser = userComponent.getUserBalance(user.userId)
        assertEquals(user.userId, updatedUser.userId)
        assertEquals(user.balance - countStocksToBuy * company.pricePerStock, updatedUser.balance)

        val updatedUserCountStocks = getUserCompanyCountStocks(user.userId, company.companyId)!!
        assertEquals(userCountStocks + countStocksToBuy, updatedUserCountStocks)

        return Pair(updatedCompany, updatedUser)
    }

    private fun createUser(userId: Long): UserBalanceDto {
        val user = userComponent.createUser(userId)
        assertEquals(userId, user.userId)
        assertEquals(0, user.balance)
        return user
    }

    private fun increaseBalance(user: UserBalanceDto, increaseAmount: Long): UserBalanceDto {
        val updatedUser = userComponent.increaseUserBalance(user.userId, increaseAmount)
        assertEquals(user.userId, updatedUser.userId)
        assertEquals(user.balance + increaseAmount, updatedUser.balance)
        return updatedUser
    }

    private fun createUserOffer(
        seller: UserBalanceDto,
        companyId: Long,
        pricePerStock: Long,
        countStocks: Int
    ): UserOfferDto {
        val sellerTotalBalance = getUserTotalBalance(seller.userId)
        val sellerCountStocks = getUserCompanyCountStocks(seller.userId, companyId) ?: 0

        val offerDto =
            userComponent.createUserOffer(seller.userId, companyId, pricePerStock, countStocks)
        assertEquals(companyId, offerDto.companyId)
        assertEquals(pricePerStock, offerDto.pricePerStock)
        assertEquals(countStocks, offerDto.countStocks)

        val updatedSellerTotalBalance = getUserTotalBalance(seller.userId)
        assertEquals(
            sellerTotalBalance - companyComponent.getCompanyInfo(companyId).pricePerStock * countStocks,
            updatedSellerTotalBalance
        )

        val updatedSellerCountStocks = getUserCompanyCountStocks(seller.userId, companyId)!!
        assertEquals(sellerCountStocks - countStocks, updatedSellerCountStocks)

        return offerDto
    }

    private fun buyUserOffer(offer: UserOfferDto, buyer: UserBalanceDto): UserBalanceDto {
        val seller = userComponent.getUserBalance(offer.userSellerId)

        val buyerTotalBalance = getUserTotalBalance(buyer.userId)
        val buyerCountStocks = getUserCompanyCountStocks(buyer.userId, offer.companyId) ?: 0

        val updatedBuyer = userComponent.buyUserOffer(offer.offerId, buyer.userId)
        assertEquals(buyer.userId, updatedBuyer.userId)

        val offerPrice = offer.countStocks * offer.pricePerStock
        assertEquals(buyer.balance - offerPrice, updatedBuyer.balance)

        val company = companyComponent.getCompanyInfo(offer.companyId)

        val updatedBuyerTotalBalance = getUserTotalBalance(buyer.userId)
        assertEquals(
            buyerTotalBalance - offerPrice + company.pricePerStock * offer.countStocks,
            updatedBuyerTotalBalance
        )

        val updatedBuyerCountStocks = getUserCompanyCountStocks(buyer.userId, offer.companyId)!!
        assertEquals(buyerCountStocks + offer.countStocks, updatedBuyerCountStocks)

        val updatedSeller = userComponent.getUserBalance(seller.userId)
        assertEquals(seller.balance + offerPrice, updatedSeller.balance)

        return updatedBuyer
    }

    @Test
    fun singleUserTwoCompanies() {
        var company1 = createCompany(1, 100)
        var company2 = createCompany(2, 50)

        company1 = addStocks(company1, 100)
        company2 = addStocks(company2, 50)

        var user = createUser(1)
        user = increaseBalance(user, 1000)

        buyCompanyStocks(company1, user, 4).let { company1 = it.first; user = it.second }

        assertEquals(1000, getUserTotalBalance(user.userId))

        company1 = changePricePerStock(company1, 50)
        assertEquals( 800, getUserTotalBalance(user.userId))

        buyCompanyStocks(company2, user, 10).let { company2 = it.first; user = it.second }

        assertEquals( 800, getUserTotalBalance(user.userId))

        company2 = changePricePerStock(company2, 100)
        assertEquals( 1300, getUserTotalBalance(user.userId))

        checkGetCompany(company2)
        checkGetCompany(company1)

        assertEquals(10, getUserCompanyCountStocks(user.userId, company2.companyId))
        assertEquals(4, getUserCompanyCountStocks(user.userId, company1.companyId))
    }

    @Test
    fun singleCompanyTwoUsers() {
        var company = createCompany(3, 10)
        company = addStocks(company, 100)

        var user1 = createUser(2)
        user1 = increaseBalance(user1, 1000)

        var user2 = createUser(3)
        user2 = increaseBalance(user2, 2000)

        buyCompanyStocks(company, user1, 20).let { company = it.first; user1 = it.second }

        company = changePricePerStock(company, 20)
        assertEquals(1200, getUserTotalBalance(user1.userId))

        val offer = createUserOffer(user1, company.companyId, 15, 10)

        user2 = buyUserOffer(offer, user2)

        assertEquals(10, getUserCompanyCountStocks(user2.userId, company.companyId))
        assertEquals(10, getUserCompanyCountStocks(user1.userId, company.companyId))
    }
}
