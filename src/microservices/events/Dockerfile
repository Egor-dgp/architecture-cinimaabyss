# Этап 1: Сборка
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Копируем ВСЕ файлы из текущей папки
COPY . .

# Собираем проект
RUN mvn clean package -DskipTests

# Этап 2: Запуск
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]