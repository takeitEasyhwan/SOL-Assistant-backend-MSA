FROM amazoncorretto:17-alpine
WORKDIR /app
COPY build/libs/external-assistant-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]