package ru.aslastin.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.aslastin.component.ManagerCommandComponent
import ru.aslastin.component.ManagerQueryComponent
import ru.aslastin.dto.*
import javax.validation.Valid

@RestController
@RequestMapping("/manager")
class ManagerController(
    @Autowired private val managerCommandComponent: ManagerCommandComponent,
    @Autowired private val managerQueryComponent: ManagerQueryComponent
) {
    @GetMapping("/membership/{membershipId}")
    fun getMembershipInfo(@PathVariable membershipId: Long): MembershipInfoDto =
        managerQueryComponent.getMembershipInfo(MembershipInfoQuery(membershipId))

    @PostMapping("/membership")
    fun createMembership(@Valid @RequestBody createCommand: CreateMembershipCommand): MembershipInfoDto =
        managerCommandComponent.createMembership(createCommand)

    @PutMapping("/membership/{membershipId}")
    fun prolongMembership(
        @PathVariable membershipId: Long,
        @Valid @RequestBody prolongMembershipDto: ProlongMembershipDto,
    ): MembershipInfoDto = managerCommandComponent.prolongMembership(
        ProlongMembershipCommand(membershipId, prolongMembershipDto.prolongTime)
    )
}
