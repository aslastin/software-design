package ru.aslastin

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import ru.aslastin.component.ManagerCommandComponent
import ru.aslastin.component.ManagerQueryComponent
import ru.aslastin.component.ReportQueryComponent
import ru.aslastin.component.TurnstileCommandComponent
import ru.aslastin.dto.CreateMembershipCommand
import ru.aslastin.dto.EnterTurnstileCommand
import ru.aslastin.dto.MembershipInfoDto
import ru.aslastin.exception.MembershipNotFoundException

@AutoConfigureMockMvc
@SpringBootTest
class WebLayerTest(@Autowired val mockMvc: MockMvc) {
    @MockkBean
    private lateinit var managerCommandComponent: ManagerCommandComponent

    @MockkBean
    private lateinit var managerQueryComponent: ManagerQueryComponent

    @MockkBean
    private lateinit var turnstileCommandComponent: TurnstileCommandComponent

    @MockkBean
    private lateinit var reportQueryComponent: ReportQueryComponent

    private fun postByUrlWithBody(url: String, body: Any) =
        MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(body))


    @Test
    fun `Абономент создается и возвращает 200`() {
        val membershipId = 228L
        val startTime = 50L
        val endTime = 200L
        every {
            managerCommandComponent.createMembership(
                CreateMembershipCommand(startTime, endTime)
            )
        } returns MembershipInfoDto(membershipId, startTime, endTime)

        mockMvc.perform(
            postByUrlWithBody(
                "/manager/membership",
                mapOf(Pair("startTime", startTime), Pair("endTime", endTime))
            )
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect {
                MockMvcResultMatchers.jsonPath("$.[0].membershipId").value(membershipId)
                MockMvcResultMatchers.jsonPath("$.[0].startTime").value(startTime)
                MockMvcResultMatchers.jsonPath("$.[0].endTime").value(endTime)
            }
    }

    @Test
    fun `При попытке войти по несуществующими membershipId вылетает NOT_FOUND`() {
        val membershipId = -5L
        val time = 10L
        every {
            turnstileCommandComponent.enter(EnterTurnstileCommand(membershipId, time))
        } throws MembershipNotFoundException(membershipId)

        mockMvc.perform(
            postByUrlWithBody(
                "/turnstile/enter",
                mapOf(Pair("membershipId", membershipId), Pair("time", time))
            )
        ).andExpect(MockMvcResultMatchers.status().isNotFound)
    }

}
