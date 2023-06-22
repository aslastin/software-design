package ru.aslastin.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import ru.aslastin.dto.PurchasedCompanyStocks
import ru.aslastin.entity.CompanyEntity
import ru.aslastin.entity.UserEntity
import ru.aslastin.entity.UserOfferEntity
import ru.aslastin.entity.UserStocksEntity


interface CompanyRepository: CrudRepository<CompanyEntity, Long>

interface UserRepository : CrudRepository<UserEntity, Long>

interface UserStocksRepository: CrudRepository<UserStocksEntity, Long> {
    @Query("""
        SELECT new ru.aslastin.dto.PurchasedCompanyStocks(us.companyId, c.pricePerStock, us.countStocks)
        FROM user_stocks us INNER JOIN companies c on us.companyId = c.companyId
        WHERE us.userId = :userId
        """
    )
    fun getUserStocksWithPriceByUserId(userId: Long): List<PurchasedCompanyStocks>?

    @Query("""
        SELECT SUM(us.countStocks * c.pricePerStock)
        FROM user_stocks us INNER JOIN companies c on us.companyId = c.companyId
        WHERE us.userId = :userId
    """)
    fun getTotalStocksPriceByUserId(userId: Long): Long?

    fun findByUserIdAndCompanyId(userId: Long, companyId: Long): UserStocksEntity?
}

interface UserOfferRepository : CrudRepository<UserOfferEntity, Long>
