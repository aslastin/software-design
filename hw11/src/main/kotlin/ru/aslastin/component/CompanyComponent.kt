package ru.aslastin.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import ru.aslastin.converter.toCompanyInfoDto
import ru.aslastin.dto.CompanyInfoDto
import ru.aslastin.entity.CompanyEntity
import ru.aslastin.exception.CompanyAlreadyExistsException
import ru.aslastin.exception.CompanyNotEnoughStocksException
import ru.aslastin.exception.CompanyNotFoundException
import ru.aslastin.exception.UserNotEnoughBalanceException
import ru.aslastin.repository.CompanyRepository
import ru.aslastin.repository.UserRepository
import javax.transaction.Transactional

@Component
class CompanyComponent(
    @Autowired private val companyRepository: CompanyRepository,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userComponent: UserComponent
) {
    @Transactional
    fun createCompany(companyId: Long, pricePerStock: Long): CompanyInfoDto {
        companyRepository.findByIdOrNull(companyId)
            ?.let { throw CompanyAlreadyExistsException(companyId) }
        return companyRepository.save(CompanyEntity(pricePerStock, 0, companyId)).toCompanyInfoDto()
    }

    @Transactional
    fun addCompanyStocks(companyId: Long, countStocksToAdd: Int): CompanyInfoDto {
        val company = findCompanyById(companyId)
        return companyRepository.save(company.copy(countStocks = company.countStocks + countStocksToAdd))
            .toCompanyInfoDto()
    }

    fun getCompanyInfo(companyId: Long): CompanyInfoDto =
        findCompanyById(companyId).toCompanyInfoDto()

    @Transactional
    fun buyStocks(companyId: Long, userBuyerId: Long, countStocksToBuy: Int) {
        val company = findCompanyById(companyId)
        val user = userComponent.findUserById(userBuyerId)
        if (countStocksToBuy > company.countStocks) {
            throw CompanyNotEnoughStocksException(companyId, countStocksToBuy, company.countStocks)
        }
        val balanceToWithdraw = company.pricePerStock * countStocksToBuy
        if (balanceToWithdraw > user.balance) {
            throw UserNotEnoughBalanceException(userBuyerId, balanceToWithdraw, user.balance)
        }
        companyRepository.save(company.copy(countStocks = company.countStocks - countStocksToBuy))
        userRepository.save(user.copy(balance = user.balance - balanceToWithdraw))
        userComponent.addUserStocks(userBuyerId, companyId, countStocksToBuy)
    }

    @Transactional
    fun changeCompanyPricePerStock(companyId: Long, newPricePerStock: Long): CompanyInfoDto {
        val company = findCompanyById(companyId)
        return companyRepository.save(company.copy(pricePerStock = newPricePerStock))
            .toCompanyInfoDto()
    }

    fun findCompanyById(companyId: Long): CompanyEntity =
        companyRepository.findByIdOrNull(companyId) ?: throw CompanyNotFoundException(companyId)
}
