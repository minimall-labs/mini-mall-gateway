FROM eclipse-temurin:21-jre

WORKDIR /app

COPY gateway/target/gateway-1.0.0-SNAPSHOT.jar /app/gateway.jar

EXPOSE 8080

ENV SERVER_PORT=8080
ENV SPRING_APPLICATION_NAME=mini-mall-gateway

ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]
