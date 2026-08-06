FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/noticore-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]