# Мини Яндекс Еда 🍔

Микросервисная платформа, имитирующая работу сервиса доставки еды (аналог **Яндекс Еда** / **Самокат** / ).

## О проекте

Это демонстрационный проект, построенный с использованием **микросервисной архитектуры**.  
Основная цель - реализовать взаимодействие между сервисами через **Kafka** и **Spring Boot RestClient**, работу с **PostgreSQL**, **Docker** и современным стеком **Spring Boot**.

### Основные сервисы

- **order-service** - создание, управление и отслеживание заказов
- **delivery-service** - логистика, статусы доставки, управление курьерами
- **payment-service** - обработка платежей
- **common-libs** - общие модели, DTO, утилиты и конфигурации

## Технологический стек

- **Язык**: Java 21
- **Фреймворк**: Spring Boot 4+
- **База данных**: PostgreSQL + Spring Data JPA
- **Межсервисное взаимодействие**: Apache Kafka, Spring Boot RestClient
- **Сборка**: Gradle (Kotlin DSL)
- **API документация**: Springdoc OpenAPI (Swagger UI)
- **Контейнеризация**: Docker + Docker Compose
- **Маппинг**: MapStruct
- **Дополнительно**: Lombok, Validation, Slf4j

## Структура проекта

```bash
mini-yandex-food/
├── order-service/
├── delivery-service/
├── payment-service/
├── common-libs/
├── docker-compose.yml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Быстрый запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/irishsea/mini-yandex-food.git
cd mini-yandex-food
```

### 2. Сборка проекта

```bash
./gradlew clean build
```

### 3. Запуск всех сервисов

```bash
docker-compose up -d
```

### Доступные сервисы после запуска

- **Swagger UI**:
  - Order Service: http://localhost:8081/swagger-ui.html
  - Delivery Service: http://localhost:8082/swagger-ui.html
  - Payment Service: http://localhost:8083/swagger-ui.html
- **PostgreSQL**: `localhost:5432`
- **Kafka**: `localhost:9092`

## Основные возможности

- Полный цикл создания и обработки заказа
- Асинхронное взаимодействие между сервисами через Kafka
- Симуляция процесса доставки в реальном времени
- Базовая обработка платежей
- Полная OpenAPI документация для каждого сервиса

## Планы развития (TODO)

- [ ] User Service + авторизация (JWT)
- [ ] CI/CD через GitHub Actions

---

**Автор**: [irishsea](https://github.com/irishsea)  
