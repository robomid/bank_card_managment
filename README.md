
# Bank card management

### Инструкция по запуску приложения




1. Клонировать репозиторий:
```bash
git clone https://github.com/robomid/bank_card_managment.git
cd bank-card-management
```

2. Настроить переменные окружения в файле .env:
```properties
# DataBase configuration
DATASOURCE_URL=connection_url
DATASOURCE_USERNAME=your_user_name
DATASOURCE_PASSWORD=your_password

# JWT configuration
JWT_SECRET_KEY=your_jwt_secret_key

# Mail configuration
SUPPORT_EMAIL=your_support_login
APP_PASSWORD=your_mail_app_secret_key
```

3. Запустить приложение с помощью Docker Compose:
```bash
docker-compose up --build
```
4. Приложение будет доступно по адресу: http://localhost:8080
5. Документация API (Swagger UI): http://localhost:8080/swagger-ui/index.html
