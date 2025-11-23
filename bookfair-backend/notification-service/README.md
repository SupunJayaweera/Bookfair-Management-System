# Notification Service

A production-ready microservice for handling notifications in the Bookfair Management System. This service consumes reservation events from Kafka and sends email notifications with QR codes to users.

## 🚀 Features

- **Email Notifications**: Send reservation confirmation emails with QR codes
- **Kafka Integration**: Consumes events from reservation service
- **Resilience**: Circuit breakers, retry mechanisms, and fallback handling
- **Database Tracking**: Stores notification history for audit and retry
- **Observability**: Health checks, metrics, and distributed tracing
- **Production-Ready**: Profile-based configuration, externalized secrets, and optimized Docker image

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Apache Kafka 3.5+
- SMTP Server (Gmail recommended for development)

## 🛠️ Tech Stack

- **Spring Boot 3.2.0**: Core framework
- **Spring Cloud Netflix Eureka**: Service discovery
- **Spring Kafka**: Event streaming
- **Spring Mail**: Email sending
- **Resilience4j**: Circuit breaker and retry patterns
- **Spring Data JPA**: Database persistence
- **Thymeleaf**: Email templating
- **ZXing**: QR code generation
- **Micrometer**: Metrics and observability
- **JUnit 5 & Mockito**: Testing

## 📁 Project Structure

```
notification-service/
├── src/
│   ├── main/
│   │   ├── java/com/bookfair/notification/
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── consumer/            # Kafka consumers
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── entity/              # JPA entities
│   │   │   ├── event/               # Event models
│   │   │   ├── exception/           # Custom exceptions
│   │   │   ├── repository/          # JPA repositories
│   │   │   ├── service/             # Business logic
│   │   │   └── NotificationServiceApplication.java
│   │   └── resources/
│   │       ├── templates/           # Email templates
│   │       ├── application.yml      # Main config
│   │       ├── application-dev.yml  # Dev profile
│   │       └── application-prod.yml # Production profile
│   └── test/                        # Unit and integration tests
├── Dockerfile                       # Multi-stage Docker build
├── pom.xml                          # Maven dependencies
└── README.md
```

## ⚙️ Configuration

### Environment Variables

Create a `.env` file based on `.env.example`:

```bash
# Database
DB_URL=jdbc:mysql://localhost:3306/bookfair_notifications
DB_USERNAME=root
DB_PASSWORD=your_password

# Email (Gmail)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9093

# Eureka
EUREKA_SERVER=http://localhost:8761/eureka/
```

### Gmail Configuration

1. Enable 2-Factor Authentication on your Gmail account
2. Generate an App Password: [Google Account Settings](https://myaccount.google.com/apppasswords)
3. Use the generated password as `MAIL_PASSWORD`

## 🚀 Running the Application

### Local Development

```bash
# 1. Clone the repository
git clone https://github.com/SupunJayaweera/Bookfair-Management-System.git
cd notification-service

# 2. Set environment variables
cp .env.example .env
# Edit .env with your configuration

# 3. Start infrastructure (Kafka, MySQL)
docker-compose -f ../docker-compose-infra.yml up -d

# 4. Run the application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Using Docker

```bash
# Build and run with Docker
docker build -t notification-service:latest .
docker run -p 8084:8084 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/bookfair_notifications \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=root \
  -e MAIL_USERNAME=your-email@gmail.com \
  -e MAIL_PASSWORD=your-app-password \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9093 \
  -e EUREKA_SERVER=http://host.docker.internal:8761/eureka/ \
  notification-service:latest
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Run specific test
./mvnw test -Dtest=EmailServiceTest
```

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8084/actuator/health
```

### Metrics (Prometheus)

```bash
curl http://localhost:8084/actuator/prometheus
```

### Notification Stats

```bash
curl http://localhost:8084/api/notifications/health
```

## 🔌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications/health` | Service health and stats |
| GET | `/api/notifications/reservation/{id}` | Get notifications by reservation |
| GET | `/api/notifications/email/{email}` | Get notifications by email |
| GET | `/api/notifications/status/{status}` | Get notifications by status |
| GET | `/actuator/health` | Actuator health check |
| GET | `/actuator/prometheus` | Prometheus metrics |

## 🔄 Kafka Topics

### Consumed Topics

- **reservation-notifications**: Receives reservation events from reservation-service

### Event Schema

```json
{
  "reservationId": 123,
  "userId": 456,
  "userEmail": "user@example.com",
  "stallIds": [1, 2, 3],
  "qrCode": "QR123456789"
}
```

## 🛡️ Resilience Patterns

### Circuit Breaker

- **Sliding Window**: 10 requests (dev), 20 requests (prod)
- **Failure Threshold**: 50%
- **Wait Duration**: 30s (dev), 60s (prod)
- **Half-Open Calls**: 3 (dev), 5 (prod)

### Retry

- **Max Attempts**: 3 (dev), 5 (prod)
- **Wait Duration**: 2s (dev), 3s (prod)
- **Exponential Backoff**: 2x multiplier

## 📈 Performance

- **Kafka Concurrency**: 3 consumers (dev), 5 consumers (prod)
- **Database Connection Pool**: 5-20 connections
- **Manual Acknowledgment**: Ensures at-least-once delivery
- **JVM Tuning**: Container-aware settings with 75% max RAM

## 🔒 Security

- **Non-root User**: Docker runs as unprivileged user
- **Externalized Secrets**: No hardcoded credentials
- **Input Validation**: All DTOs validated
- **Exception Handling**: Global exception handler

## 🐛 Troubleshooting

### Email Not Sending

1. Check Gmail App Password is correct
2. Verify firewall allows port 587
3. Check logs: `tail -f logs/notification-service.log`

### Kafka Connection Issues

1. Verify Kafka is running: `docker ps | grep kafka`
2. Check bootstrap servers configuration
3. Review consumer group status

### Database Connection

1. Ensure MySQL is running
2. Verify database exists: `bookfair_notifications`
3. Check credentials in environment variables

## 📝 Development

### Adding New Notification Types

1. Add enum value to `NotificationType`
2. Create Thymeleaf template in `resources/templates/`
3. Implement service method in `EmailService`
4. Add Kafka listener if needed

### Running in IDE

- **IntelliJ IDEA**: Run `NotificationServiceApplication` with `-Dspring.profiles.active=dev`
- **VS Code**: Use Spring Boot Dashboard extension

## 📄 License

This project is part of the Bookfair Management System.

## 👥 Contributors

- Bookfair Development Team

## 📞 Support

For issues and questions:
- Email: dev@bookfair.lk
- GitHub Issues: [Create Issue](https://github.com/SupunJayaweera/Bookfair-Management-System/issues)
