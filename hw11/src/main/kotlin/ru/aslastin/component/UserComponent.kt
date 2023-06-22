package ru.aslastin.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import ru.aslastin.converter.toUserBalanceDto
import ru.aslastin.converter.toUserOfferDto
import ru.aslastin.dto.UserBalanceDto
import ru.aslastin.dto.UserOfferDto
import ru.aslastin.dto.UserStocksDto
import ru.aslastin.dto.UserTotalBalanceDto
import ru.aslastin.entity.UserEntity
import ru.aslastin.entity.UserOfferEntity
import ru.aslastin.entity.UserStocksEntity
import ru.aslastin.exception.*
import ru.aslastin.repository.UserOfferRepository
import ru.aslastin.repository.UserRepository
import ru.aslastin.repository.UserStocksRepository
import javax.transaction.Transactional

@Component
class UserComponent(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userOfferRepository: UserOfferRepository,
    @Autowired private val userStocksRepository: UserStocksRepository
) {
    @Transactional
    fun createUser(userId: Long): UserBalanceDto {
        userRepository.findByIdOrNull(userId)?.let { throw UserAlreadyExistsException(userId) }
        return userRepository.save(UserEntity(0L, userId)).toUserBalanceDto()
    }

    @Transactional
    fun increaseUserBalance(userId: Long, increaseAmount: Long): UserBalanceDto {
        val user = findUserById(userId)
        return userRepository.save(user.copy(balance = user.balance + increaseAmount))
            .toUserBalanceDto()
    }

    fun getUserBalance(userId: Long): UserBalanceDto = findUserById(userId).toUserBalanceDto()

    fun getUserTotalBalance(userId: Long): UserTotalBalanceDto {
        val user = findUserById(userId)
        val totalStocksPrice = userStocksRepository.getTotalStocksPriceByUserId(userId) ?: 0L
        return UserTotalBalanceDto(userId, user.balance + totalStocksPrice)
    }

    fun getUserStocks(userId: Long): UserStocksDto {
        findUserById(userId)
        return UserStocksDto(
            userId,
            userStocksRepository.getUserStocksWithPriceByUserId(userId) ?: emptyList()
        )
    }

    @Transactional
    fun createUserOffer(
        userSellerId: Long,
        companyId: Long,
        pricePerStock: Long,
        countStocks: Int
    ): UserOfferDto {
        val userStocks = userStocksRepository.findByUserIdAndCompanyId(userSellerId, companyId)
        if (userStocks == null || userStocks.countStocks < countStocks) {
            val actualStocksCount = userStocks?.countStocks ?: 0
            throw UserNotEnoughStocksCountException(userSellerId, countStocks, actualStocksCount)
        }
        val offerEntity = userOfferRepository.save(
            UserOfferEntity(userSellerId, companyId, pricePerStock, countStocks)
        )
        userStocksRepository.save(userStocks.copy(countStocks = userStocks.countStocks - countStocks))
        return offerEntity.toUserOfferDto()
    }

    @Transactional
    fun buyUserOffer(offerId: Long, userBuyerId: Long): UserBalanceDto {
        val buyer = findUserById(userBuyerId)
        val offer = findOfferById(offerId)
        val totalPrice = offer.pricePerStock * offer.countStocks
        if (buyer.balance < totalPrice) {
            throw UserNotEnoughBalanceException(userBuyerId, totalPrice, buyer.balance)
        }
        val updatedBuyer = buyer.copy(balance = buyer.balance - totalPrice)
        userRepository.save(updatedBuyer)
        addUserStocks(userBuyerId, offer.companyId, offer.countStocks)

        val seller = findUserById(offer.userSellerId)
        userRepository.save(seller.copy(balance = seller.balance + totalPrice))

        userOfferRepository.deleteById(offerId)

        return updatedBuyer.toUserBalanceDto()
    }

    // suppose userId exists in DB && called inside transactional block
    fun addUserStocks(userId: Long, companyId: Long, countStocksToAdd: Int) {
        val userStocks = userStocksRepository.findByUserIdAndCompanyId(userId, companyId)
            ?: UserStocksEntity(userId, companyId, 0)
        userStocksRepository.save(userStocks.copy(countStocks = userStocks.countStocks + countStocksToAdd))
    }

    fun findUserById(userId: Long) =
        userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException(userId)

    fun findOfferById(offerId: Long) =
        userOfferRepository.findByIdOrNull(offerId) ?: throw UserOfferNotFoundException(offerId)

}
