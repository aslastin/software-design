package ru.aslastin.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.aslastin.component.CompanyComponent
import ru.aslastin.dto.AddStocksDto
import ru.aslastin.dto.BuyStocksDto
import ru.aslastin.dto.CompanyInfoDto
import ru.aslastin.dto.CreateCompanyDto
import javax.validation.Valid


@RestController
@RequestMapping("/company")
class CompanyController(@Autowired private val companyComponent: CompanyComponent) {
    @PostMapping
    fun create(@Valid @RequestBody createCompanyDto: CreateCompanyDto): CompanyInfoDto =
        companyComponent.createCompany(createCompanyDto.companyId, createCompanyDto.pricePerStock)

    @PostMapping("/{companyId}")
    fun addStocks(
        @PathVariable companyId: Long,
        @Valid @RequestBody addStocksDto: AddStocksDto
    ): CompanyInfoDto =
        companyComponent.addCompanyStocks(companyId, addStocksDto.countStocksToAdd)

    @GetMapping("/{companyId}")
    fun getInfo(@PathVariable companyId: Long): CompanyInfoDto =
        companyComponent.getCompanyInfo(companyId)

    @PostMapping("/market")
    fun buyStocks(@Valid @RequestBody buyStocksDto: BuyStocksDto) = companyComponent.buyStocks(
        buyStocksDto.companyId, buyStocksDto.userBuyerId, buyStocksDto.countStocksToBuy,
    )
}
