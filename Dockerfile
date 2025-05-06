FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/bank-card-management-0.0.1-SNAPSHOT.jar app.jar
COPY .env /app/.env
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
