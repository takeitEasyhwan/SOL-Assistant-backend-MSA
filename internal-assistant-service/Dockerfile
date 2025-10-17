FROM openjdk:17-jdk

WORKDIR /app

# secrets로 workflow에서 복원된 yml 파일 포함
COPY src/main/resources/application.yml ./application.yml
COPY src/main/resources/application-common.yml ./application-common.yml

COPY build/libs/internal-assistant-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
