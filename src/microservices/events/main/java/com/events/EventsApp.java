package com.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication - ВКЛЮЧАЕТ ВСЕ: веб-сервер, Kafka, автоконфигурацию
// Без этой аннотации ничего не заработает
@SpringBootApplication
public class EventsApp {
    public static void main(String[] args) {
        // Запускаем Spring Boot приложение
        SpringApplication.run(EventsApp.class, args);
        System.out.println("✅ Сервис запущен! Доступен на http://localhost:8080");
    }
}