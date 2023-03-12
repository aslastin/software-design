package ru.aslastin.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.aslastin.dto.*
import ru.aslastin.event.CreateMembershipEvent
import ru.aslastin.event.ProlongMembershipEvent
import ru.aslastin.exception.MembershipBadTimeIntervalException
import ru.aslastin.exception.MembershipNotFoundException
import ru.aslastin.repository.CreateMembershipRepository
import ru.aslastin.repository.ProlongMembershipRepository

@Component
class ManagerCommandComponent(
    @Autowired private val createMembershipRepository: CreateMembershipRepository,
    @Autowired private val prolongMembershipRepository: ProlongMembershipRepository,
    @Autowired private val managerQueryComponent: ManagerQueryComponent
) {
    fun createMembership(createMembershipCommand: CreateMembershipCommand): MembershipInfoDto {
        val (startTime, endTime) = createMembershipCommand
        if (endTime <= startTime) {
            throw MembershipBadTimeIntervalException(startTime, endTime)
        }
        val id = createMembershipRepository.save(CreateMembershipEvent(startTime)).membershipId!!
        prolongMembershipRepository.save(ProlongMembershipEvent(id, endTime - startTime))
        return MembershipInfoDto(id, startTime, endTime)
    }

    fun prolongMembership(prolongMembershipCommand: ProlongMembershipCommand): MembershipInfoDto {
        val (id, prolongTime) = prolongMembershipCommand
        val infoDto = managerQueryComponent.getMembershipInfo(MembershipInfoQuery(id))
        prolongMembershipRepository.save(ProlongMembershipEvent(id, prolongTime))
        return MembershipInfoDto(
            infoDto.membershipId,
            infoDto.startTime,
            infoDto.endTime + prolongTime
        )
    }
}

@Component
class ManagerQueryComponent(
    @Autowired private val createMembershipRepository: CreateMembershipRepository,
    @Autowired private val prolongMembershipRepository: ProlongMembershipRepository
) {
    fun getMembershipInfo(membershipInfoQuery: MembershipInfoQuery): MembershipInfoDto {
        val id = membershipInfoQuery.membershipId
        val startTime = getStartTime(StartTimeQuery(id))
        val sumProlongTime = prolongMembershipRepository.sumProlongTimeByMembershipId(id)
        return MembershipInfoDto(id, startTime, startTime + sumProlongTime)
    }

    fun getStartTime(startTimeQuery: StartTimeQuery): Long {
        val id = startTimeQuery.membershipId
        val createMembershipEvent = createMembershipRepository.findById(id)
        if (createMembershipEvent.isEmpty) {
            throw MembershipNotFoundException(id)
        }
        return createMembershipEvent.get().startTime
    }
}
