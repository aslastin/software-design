package ru.aslastin.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.aslastin.component.CompanyComponent
import ru.aslastin.dto.ChangePricePerStockDto
import ru.aslastin.dto.CompanyInfoDto
import javax.validation.Valid

@RestController
@RequestMapping("/admin")
class AdminController(@Autowired private val companyComponent: CompanyComponent) {
    @PutMapping("/company/{companyId}")
    fun changePricePerStock(
        @PathVariable companyId: Long,
        @Valid @RequestBody changePricePerStockDto: ChangePricePerStockDto
    ): CompanyInfoDto = companyComponent.changeCompanyPricePerStock(
        companyId,
        changePricePerStockDto.newPricePerStock
    )
}
