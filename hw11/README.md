# Stock-Exchange Application

Веб-интерфейс приложения: http://localhost:8080/stock-exchange/swagger-ui/index.html

Докер образ собирается с помощью скрипта [build_app_image.sh](build_app_image.sh).

Для тестирования приложения были написаны [интеграционные тесты](src/test/kotlin/ru/aslastin/IntegrationTest.kt) 
с использованием [фреймворка testcontainers](https://www.testcontainers.org/).

## Примечание

Корректность операций не гарантируется в случае параллельной работы над каким-то одним интстансом 
(например, админ меняет цену акций компании, а пользователь в это время покупает ее акции).
