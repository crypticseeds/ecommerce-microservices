FROM eclipse-temurin:17.0.4.1_1-jre as builder
WORKDIR /app
COPY target/*.jar notification-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "notification-service.jar"]