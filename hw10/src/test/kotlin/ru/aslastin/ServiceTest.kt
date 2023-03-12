package ru.aslastin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.aslastin.controller.ManagerController
import ru.aslastin.controller.ReportController
import ru.aslastin.controller.TurnstileController
import ru.aslastin.dto.CreateMembershipCommand
import ru.aslastin.dto.EnterTurnstileCommand
import ru.aslastin.dto.ExitTurnstileCommand
import ru.aslastin.dto.ProlongMembershipDto
import ru.aslastin.exception.BadVisitIntervalException
import kotlin.math.abs

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTest(
    @Autowired private val managerController: ManagerController,
    @Autowired private val reportController: ReportController,
    @Autowired private val turnstileController: TurnstileController
) {

    private fun timeToString(time: Long) =
        reportController.reportQueryComponent.timeToString(time)

    private fun assertDoubleEquals(expected: Double, actual: Double) =
        assertTrue(abs(expected - actual) < 1e-3) {
            "expected=$expected, actual=$actual"
        }

    private fun createMembership(startTime: Long, endTime: Long): Long {
        val (membershipId, actualStartTime, actualEndTime) = managerController.createMembership(
            CreateMembershipCommand(startTime, endTime)
        )
        assertEquals(startTime, actualStartTime)
        assertEquals(endTime, actualEndTime)
        return membershipId
    }

    private fun prolongMembership(
        membershipId: Long,
        startTime: Long,
        endTime: Long,
        prolongTime: Long
    ) {
        val (actualMembershipId, actualStartTime, actualEndTime) = managerController.prolongMembership(
            membershipId, ProlongMembershipDto(prolongTime)
        )
        assertEquals(membershipId, actualMembershipId)
        assertEquals(startTime, actualStartTime)
        assertEquals(endTime + prolongTime, actualEndTime)
    }

    private fun checkMembershipInfo(
        membershipId: Long,
        expectedStartTime: Long,
        expectedEndTime: Long
    ) {
        val (actualMembershipId, actualStartTime, actualEndTime) = managerController.getMembershipInfo(
            membershipId
        )
        assertEquals(membershipId, actualMembershipId)
        assertEquals(expectedStartTime, actualStartTime)
        assertEquals(expectedEndTime, actualEndTime)
    }

    private fun enterTurnstile(membershipId: Long, time: Long): Long {
        val (visitId, actualMembershipId, actualTime) = turnstileController.enter(
            EnterTurnstileCommand(membershipId, time)
        )
        assertEquals(membershipId, actualMembershipId)
        assertEquals(time, actualTime)
        return visitId
    }

    private fun exitTurnStile(visitId: Long, membershipId: Long, time: Long) =
        turnstileController.exit(ExitTurnstileCommand(visitId, membershipId, time))

    private fun checkDayToCount(membershipId: Long, expectedDayToCount: Map<String, Int>) {
        val (actualMembershipId, actualDayToCount) = reportController.getDayToCountStat(membershipId).body!!
        assertEquals(membershipId, actualMembershipId)
        assertEquals(expectedDayToCount, actualDayToCount)
    }

    private fun checkAvgFrequency(membershipId: Long, expectedFrequency: Double) {
        val (actualMembershipId, actualFrequency) = reportController.getAvgFrequency(membershipId)
        assertEquals(membershipId, actualMembershipId)
        assertDoubleEquals(expectedFrequency, actualFrequency)
    }

    private fun checkAvgDuration(membershipId: Long, expectedDuration: Double) {
        val (actualMembershipId, actualDuration) = reportController.getAvgDuration(membershipId)
        assertEquals(membershipId, actualMembershipId)
        assertDoubleEquals(expectedDuration, actualDuration)
    }

    @Test
    fun singleMembership() {
        val startTime = 1L
        var endTime = 100L
        val membershipId = createMembership(startTime, endTime)

        val visitId1 = enterTurnstile(membershipId, 25)
        assertThrows(BadVisitIntervalException::class.java) {
            exitTurnStile(visitId1, membershipId, 20)
        }
        exitTurnStile(visitId1, membershipId, 50)

        checkDayToCount(membershipId, mapOf(Pair(timeToString(25), 1)))
        checkAvgFrequency(membershipId, 0.0)
        checkAvgDuration(membershipId, 25.0)

        prolongMembership(membershipId, startTime, endTime, 200)
        endTime += 200

        val visitId2 = enterTurnstile(membershipId, 100)
        exitTurnStile(visitId2, membershipId, 200)

        checkDayToCount(membershipId, mapOf(Pair(timeToString(25), 2)))
        checkAvgFrequency(membershipId, 75.0)
        checkAvgDuration(membershipId, 62.5)

        prolongMembership(membershipId, startTime, endTime, 1_000_000_000);
        endTime += 1_000_000_000

        checkMembershipInfo(membershipId, startTime, endTime)

        val visitId3 = enterTurnstile(membershipId, 500_000_000)
        exitTurnStile(visitId3, membershipId, 700_000_000)

        checkDayToCount(
            membershipId,
            mapOf(Pair(timeToString(25), 2), Pair(timeToString(500_000_000), 1))
        )
        checkAvgFrequency(membershipId, 249_999_987.5)
        checkAvgDuration(membershipId, 66_666_708.3333)

        checkMembershipInfo(membershipId, startTime, endTime)
    }

    @Test
    fun twoMemberships() {
        val startTime1 = 10L
        var endTime1 = 200L
        val membershipId1 = createMembership(startTime1, endTime1)

        val startTime2 = 100L
        val endTime2 = 500L
        val membershipId2 = createMembership(startTime2, endTime2)

        val visitId21 = enterTurnstile(membershipId2, 120)
        val visitId11 = enterTurnstile(membershipId1, 150)

        exitTurnStile(visitId21, membershipId2, 140)

        checkDayToCount(membershipId2, mapOf(Pair(timeToString(120), 1)))
        checkAvgDuration(membershipId2, 20.0)
        checkAvgFrequency(membershipId2, 0.0)

        exitTurnStile(visitId11, membershipId1, 250)

        checkDayToCount(membershipId1, mapOf(Pair(timeToString(150), 1)))
        checkAvgFrequency(membershipId1, 0.0)
        checkAvgDuration(membershipId1, 100.0)

        val visitId22 = enterTurnstile(membershipId2, 300)

        prolongMembership(membershipId1, startTime1, endTime1, 1000L)
        endTime1 += 1000L

        exitTurnStile(visitId22, membershipId2, 450)

        checkDayToCount(membershipId2, mapOf(Pair(timeToString(120), 2)))
        checkAvgDuration(membershipId2, 85.0)
        checkAvgFrequency(membershipId2, 180.0)

        enterTurnstile(membershipId2, 469)

        val visitId12 = enterTurnstile(membershipId1, 600)
        exitTurnStile(visitId12, membershipId1, 900)

        checkDayToCount(membershipId1, mapOf(Pair(timeToString(150), 2)))
        checkAvgFrequency(membershipId1, 450.0)
        checkAvgDuration(membershipId1, 200.0)

        checkMembershipInfo(membershipId2, startTime2, endTime2)
    }

    @Test
    fun membershipNoEvents() {
        val startTime = 1L
        val endTime = 50L
        val membershipId = createMembership(startTime, endTime)

        checkMembershipInfo(membershipId, startTime, endTime)

        checkDayToCount(membershipId, mapOf())
        checkAvgFrequency(membershipId, 0.0)

        checkMembershipInfo(membershipId, startTime, endTime)

        checkAvgDuration(membershipId, 0.0)
    }

    @Test
    fun contextLoads() {
    }
}
