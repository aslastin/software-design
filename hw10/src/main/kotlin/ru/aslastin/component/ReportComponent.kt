package ru.aslastin.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.aslastin.dto.*
import ru.aslastin.repository.TurnstileEnterRepository
import ru.aslastin.repository.TurnstileExitRepository
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Component
class ReportQueryComponent(
    @Autowired private val turnstileEnterRepository: TurnstileEnterRepository,
    @Autowired private val turnstileExitRepository: TurnstileExitRepository,
    @Autowired private val managerQueryComponent: ManagerQueryComponent,
    @Autowired private val simpleDateFormat: SimpleDateFormat
) {
    private val statByMembershipId: MutableMap<Long, MembershipStat> = HashMap()

    fun getDayToCount(dayToCountQuery: DayToCountQuery) = DayToCountDto(
        dayToCountQuery.membershipId,
        checkAndGetStat(dayToCountQuery.membershipId).getDayToCount()
    )

    fun getAvgFrequency(avgFrequencyQuery: AvgFrequencyQuery) = AvgFrequencyDto(
        avgFrequencyQuery.membershipId,
        checkAndGetStat(avgFrequencyQuery.membershipId).getAvgFrequency()
    )

    fun getAvgDuration(avgDurationQuery: AvgDurationQuery) = AvgDurationDto(
        avgDurationQuery.membershipId,
        checkAndGetStat(avgDurationQuery.membershipId).getAvgDuration()
    )

    private fun checkAndGetStat(membershipId: Long): MembershipStat {
        // membershipId exists?
        managerQueryComponent.getStartTime(StartTimeQuery(membershipId))

        statByMembershipId.computeIfAbsent(membershipId, ::MembershipStat)

        return statByMembershipId[membershipId]!!
    }

    private class MembershipStat(val membershipId: Long) {
        val dayToCountMap: MutableMap<String, Int> = HashMap()
        var dayToCountLastTime = -1L

        var frequencySum = 0L
        var frequencyCount = 0
        var frequencyLastVisitId = -1L

        var durationSum = 0L
        var durationCount = 0
        var durationLastVisitId = -1L
    }

    private fun MembershipStat.getDayToCount(): Map<String, Int> {
        val enterEvents = turnstileEnterRepository.findAllByMembershipIdAndTimeAfter(
            membershipId,
            dayToCountLastTime
        )

        enterEvents.groupingBy { timeToString(it.time) }
            .eachCount()
            .forEach { dayToCountMap.merge(it.key, it.value, Int::plus) }

        enterEvents.map { it.time }.maxByOrNull { it }?.let { dayToCountLastTime = it }

        return dayToCountMap
    }

    private fun MembershipStat.getAvgFrequency(): Double {
        val enterEvents = turnstileEnterRepository.findAllByMembershipIdAndVisitIdAfter(
            membershipId,
            frequencyLastVisitId
        )

        frequencyCount += enterEvents.size - 1
        for (i in 0 until enterEvents.size - 1) {
            frequencySum += enterEvents[i + 1].time - enterEvents[i].time
        }

        enterEvents.getOrNull(enterEvents.size - 2)?.let { frequencyLastVisitId = it.visitId!! }

        return avg(frequencySum, frequencyCount)
    }

    private fun MembershipStat.getAvgDuration(): Double {
        val exitEventByVisitId = turnstileExitRepository.findAllByMembershipIdAndVisitIdAfter(
            membershipId,
            durationLastVisitId
        ).associateBy { it.visitId }

        val (sum, count) = turnstileEnterRepository.findAllByMembershipIdAndVisitIdAfter(
            membershipId,
            durationLastVisitId
        )
            .map { Pair(it, exitEventByVisitId[it.visitId]) }
            .filter { it.second != null }
            .map { it.second!!.time - it.first.time }
            .fold(Pair(0L, 0)) { acc, x -> Pair(acc.first + x, acc.second + 1) }
        durationSum += sum
        durationCount += count

        exitEventByVisitId.map { it.key }.maxByOrNull { it }?.let { durationLastVisitId = it }

        return avg(durationSum, durationCount)
    }

    private fun avg(sum: Long, count: Int): Double =
        if (count == 0) 0.0 else sum.toDouble().div(count)

    fun timeToString(time: Long): String =
        simpleDateFormat.format(Date.from(Instant.ofEpochSecond(time)))
}
