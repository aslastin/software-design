package ru.aslastin.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.aslastin.component.UserComponent
import ru.aslastin.dto.*
import javax.validation.Valid

@RestController
@RequestMapping("/user")
class UserController(@Autowired private val userComponent: UserComponent) {
    @PostMapping
    fun create(@RequestBody createUserDto: CreateUserDto): UserBalanceDto =
        userComponent.createUser(createUserDto.userId)

    @PostMapping("/{userId}/balance")
    fun increaseBalance(
        @PathVariable userId: Long,
        @Valid @RequestBody increaseUserBalanceDto: IncreaseUserBalanceDto
    ): UserBalanceDto =
        userComponent.increaseUserBalance(userId, increaseUserBalanceDto.increaseAmount)

    @GetMapping("/{userId}/balance")
    fun getBalance(@PathVariable userId: Long): UserBalanceDto =
        userComponent.getUserBalance(userId)

    @GetMapping("/{userId}/balance/total")
    fun getTotalBalance(@PathVariable userId: Long): UserTotalBalanceDto =
        userComponent.getUserTotalBalance(userId)

    @GetMapping("/{userId}/stocks")
    fun getUserStocks(@PathVariable userId: Long): ResponseEntity<UserStocksDto> =
        ResponseEntity.ok(userComponent.getUserStocks(userId))

    @PostMapping("/market/sell")
    fun createOffer(@Valid @RequestBody createUserOfferDto: CreateUserOfferDto): UserOfferDto =
        userComponent.createUserOffer(
            createUserOfferDto.userSellerId,
            createUserOfferDto.companyId,
            createUserOfferDto.pricePerStock,
            createUserOfferDto.countStocks
        )

    @PostMapping("/market/buy")
    fun buyOffer(@RequestBody buyUserOfferDto: BuyUserOfferDto): UserBalanceDto =
        userComponent.buyUserOffer(buyUserOfferDto.offerId, buyUserOfferDto.userBuyerId)
}
