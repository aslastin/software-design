package ru.aslastin.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.aslastin.dto.EnterTurnstileCommand
import ru.aslastin.dto.EnterTurnstileDto
import ru.aslastin.dto.ExitTurnstileCommand
import ru.aslastin.dto.MembershipInfoQuery
import ru.aslastin.event.TurnstileEnterEvent
import ru.aslastin.event.TurnstileExitEvent
import ru.aslastin.exception.*
import ru.aslastin.repository.TurnstileEnterRepository
import ru.aslastin.repository.TurnstileExitRepository

@Component
class TurnstileCommandComponent(
    @Autowired private val turnstileEnterRepository: TurnstileEnterRepository,
    @Autowired private val turnstileExitRepository: TurnstileExitRepository,
    @Autowired private val managerQueryComponent: ManagerQueryComponent
) {
    fun enter(enterCommand: EnterTurnstileCommand): EnterTurnstileDto {
        val (id, time) = enterCommand
        val membershipInfoDto = managerQueryComponent.getMembershipInfo(MembershipInfoQuery(id))
        if (time > membershipInfoDto.endTime) {
            throw MembershipExpiredException(id, membershipInfoDto.endTime, time)
        }
        val visitId = turnstileEnterRepository.save(TurnstileEnterEvent(id, time)).visitId!!
        return EnterTurnstileDto(visitId, id, time)
    }

    fun exit(exitCommand: ExitTurnstileCommand) {
        val (visitId, id, time) = exitCommand
        checkOnExit(visitId, id, time)
        turnstileExitRepository.save(TurnstileExitEvent(visitId, id, time))
    }

    private fun checkOnExit(visitId: Long, membershipId: Long, exitTime: Long) {
        val enterEvent = turnstileEnterRepository.findById(visitId)
        if (enterEvent.isEmpty) {
            throw VisitIdNotFoundException(visitId)
        }

        val (expectedMembershipId, enterTime, _) = enterEvent.get();
        if (expectedMembershipId != membershipId) {
            throw MembershipMismatchedException(expectedMembershipId, membershipId)
        }

        val exitEvent = turnstileExitRepository.findById(visitId)
        if (exitEvent.isPresent) {
            throw AlreadyExitedException(visitId)
        }

        if (enterTime > exitTime) {
            throw BadVisitIntervalException(enterTime, exitTime)
        }
    }
}
