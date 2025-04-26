FROM eclipse-temurin:21.0.5_11-jdk-alpine

RUN apk --no-cache add curl

WORKDIR /application

COPY ./target/budgetapi-0.0.1-SNAPSHOT.jar /application

RUN addgroup --system juser

RUN adduser -S -s /bin/false -G juser juser

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.15.0/opentelemetry-javaagent.jar /application/opentelemetry-javaagent.jar

RUN chown -R juser:juser /application

USER juser

HEALTHCHECK --interval=5s --timeout=3s CMD curl --fail http://localhost:8080/api/actuator/health || exit 1

CMD ["java", "-XX:+UseG1GC", "-javaagent:./opentelemetry-javaagent.jar", "-Dotel.instrumentation.common.default-enabled=false", "-Dotel.instrumentation.micrometer.enabled=true", "-Dotel.instrumentation.spring-boot-actuator-autoconfigure.enabled=true", "-jar", "budgetapi-0.0.1-SNAPSHOT.jar"]