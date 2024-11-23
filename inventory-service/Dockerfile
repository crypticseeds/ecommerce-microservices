FROM eclipse-temurin:17.0.4.1_1-jre as builder
WORKDIR /app
COPY target/*.jar inventory-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]