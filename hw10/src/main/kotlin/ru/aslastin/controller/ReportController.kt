package ru.aslastin.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.aslastin.component.ReportQueryComponent
import ru.aslastin.dto.*

@RestController
@RequestMapping("/report")
class ReportController(@Autowired val reportQueryComponent: ReportQueryComponent) {
    @GetMapping("/stat/dayToCount/{membershipId}")
    fun getDayToCountStat(@PathVariable membershipId: Long): ResponseEntity<DayToCountDto> =
        ResponseEntity.ok(reportQueryComponent.getDayToCount(DayToCountQuery(membershipId)))

    @GetMapping("/stat/avgFrequency/{membershipId}")
    fun getAvgFrequency(@PathVariable membershipId: Long): AvgFrequencyDto =
        reportQueryComponent.getAvgFrequency(AvgFrequencyQuery(membershipId))

    @GetMapping("/stat/avgDuration/{membershipId}")
    fun getAvgDuration(@PathVariable membershipId: Long): AvgDurationDto =
        reportQueryComponent.getAvgDuration(AvgDurationQuery(membershipId))
}
