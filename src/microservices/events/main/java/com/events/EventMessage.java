package com.events;

import lombok.Data;
import java.time.LocalDateTime;

// @Data от Lombok - автоматически создает геттеры/сеттеры
// Без этой аннотации придется писать 20+ строк кода вручную
@Data
public class EventMessage {
    private String id;           // Уникальный ID события
    private String type;         // Тип: "user", "payment", "movie"
    private String data;         // Данные события (любой текст)
    private LocalDateTime time;  // Время создания
    
    // Конструктор создает ID и время автоматически
    public EventMessage() {
        this.id = java.util.UUID.randomUUID().toString();
        this.time = LocalDateTime.now();
    }
}