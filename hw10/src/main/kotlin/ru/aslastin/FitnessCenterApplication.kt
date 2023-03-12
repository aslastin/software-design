package ru.aslastin

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import ru.aslastin.component.ReportQueryComponent
import ru.aslastin.dto.AvgDurationQuery
import ru.aslastin.dto.AvgFrequencyQuery
import ru.aslastin.dto.DayToCountQuery
import ru.aslastin.repository.CreateMembershipRepository
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

@SpringBootApplication
class FitnessCenterApplication {
    @Bean
    fun simpleDateFormat(): SimpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")

    @Bean
    fun initReportQueryComponent(
        reportQueryComponent: ReportQueryComponent,
        createMembershipRepository: CreateMembershipRepository
    ) = ApplicationRunner {
        createMembershipRepository.findAll()
            .map { it.membershipId!! }
            .forEach {
                reportQueryComponent.getDayToCount(DayToCountQuery(it))
                reportQueryComponent.getAvgFrequency(AvgFrequencyQuery(it))
                reportQueryComponent.getAvgDuration(AvgDurationQuery(it))
            }
    }
}

fun main(args: Array<String>) {
    runApplication<FitnessCenterApplication>(*args)
}
