package ru.aslastin.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.aslastin.component.TurnstileCommandComponent
import ru.aslastin.dto.EnterTurnstileCommand
import ru.aslastin.dto.EnterTurnstileDto
import ru.aslastin.dto.ExitTurnstileCommand

@RestController
@RequestMapping("/turnstile")
class TurnstileController(@Autowired private val turnstileCommandComponent: TurnstileCommandComponent) {
    @PostMapping("/enter")
    fun enter(@RequestBody enterCommand: EnterTurnstileCommand): EnterTurnstileDto =
        turnstileCommandComponent.enter(enterCommand)

    @PostMapping("/exit")
    fun exit(@RequestBody exitCommand: ExitTurnstileCommand) =
        turnstileCommandComponent.exit(exitCommand)
}
