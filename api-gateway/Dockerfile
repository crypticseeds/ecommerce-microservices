FROM eclipse-temurin:17.0.4.1_1-jre as builder
WORKDIR /app
COPY target/*.jar api-gateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]