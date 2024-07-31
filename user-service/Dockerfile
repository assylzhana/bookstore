FROM openjdk:17
WORKDIR /app
COPY target/*.jar user-service.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "user-service.jar"]