FROM eclipse-temurin:17.0.4.1_1-jre as builder
WORKDIR /app
COPY target/*.jar discovery-server.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "discovery-server.jar"]