package ru.aslastin.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {
    @Bean
    fun swagger(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Stock Exchange")
                .description("Документация по API. Описание задания можно найти [здесь](https://docs.yandex.ru/docs/view?url=ya-disk-public%3A%2F%2FGVn0a%2F7x94EV9iFSZwzwX0Mx7ApURnneMbxXmHWvNUbtfTaOSUS5iE%2F9omDGjqKkq%2FJ6bpmRyOJonT3VoXnDag%3D%3D%3A%2Flabs2.docx&name=labs2.docx).")
        )
}
