package com.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// @Service - Spring понимает, что это сервисный компонент
// @RequiredArgsConstructor - автоматически создает конструктор с KafkaTemplate
// @Slf4j - для логирования (пишет в консоль)
@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    // KafkaTemplate - готовый инструмент для отправки в Kafka
    // Spring Boot создает его автоматически из настроек application.yml
    private final KafkaTemplate<String, EventMessage> kafkaTemplate;
    
    // Метод отправки события
    public void sendEvent(String type, String data) {
        // 1. Создаем сообщение
        EventMessage message = new EventMessage();
        message.setType(type);
        message.setData(data);
        
        // 2. Отправляем в Kafka
        // "events-topic" - название очереди
        // type - ключ (например "user" или "payment")
        // message - само сообщение
        kafkaTemplate.send("events-topic", type, message);
        
        // 3. Логируем
        log.info("📤 Отправлено в Kafka: {} - {}", type, data);
    }
}