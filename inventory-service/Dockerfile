FROM openjdk:17
WORKDIR /app
COPY target/*.jar inventory-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]