FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/noticore-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]