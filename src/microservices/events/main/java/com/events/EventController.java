package com.events;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// @RestController - говорит Spring, что это REST контроллер
// @RequestMapping("/api") - все методы будут начинаться с /api
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {
    
    // Внедряем EventService (Spring делает это автоматически)
    private final EventService eventService;
    
    // GET /api/health - проверка работоспособности
    @GetMapping("/health")
    public String health() {
        return "OK";  // Просто возвращаем "OK"
    }
    
    // POST /api/event - создание события
    // @RequestBody - берет JSON из тела запроса
    @PostMapping("/event")
    public String createEvent(@RequestBody EventRequest request) {
        // Вызываем сервис для отправки в Kafka
        eventService.sendEvent(request.getType(), request.getData());
        return "Событие отправлено в Kafka";
    }
    
    // Внутренний класс для приема запроса
    // Нужен только для этого контроллера
    public static class EventRequest {
        private String type;
        private String data;
        
        // Геттеры и сеттеры (можно заменить на @Data)
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }
}