package com.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// @Component - Spring понимает, что это компонент
// @Slf4j - для логирования
@Component
@Slf4j
public class KafkaConsumer {
    
    // @KafkaListener - СЛУШАЕТ указанный топик
    // "events-topic" - откуда читать
    // "my-group" - имя группы потребителей
    @KafkaListener(topics = "events-topic", groupId = "my-group")
    public void listen(EventMessage message) {
        // Этот метод ВЫЗЫВАЕТСЯ АВТОМАТИЧЕСКИ при появлении нового сообщения
        log.info("📥 Получено из Kafka: ID={}, Тип={}, Данные={}, Время={}",
                message.getId(), message.getType(), message.getData(), message.getTime());
        
        // Здесь можно добавить обработку:
        // - Сохранить в базу данных
        // - Отправить уведомление
        // - Обновить статистику
        // - и т.д.
    }
}