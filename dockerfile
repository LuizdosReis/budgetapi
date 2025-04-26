FROM eclipse-temurin:21.0.5_11-jdk-alpine AS dependencies

WORKDIR /application

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.15.0/opentelemetry-javaagent.jar /application/opentelemetry-javaagent.jar

COPY mvnw .
COPY .mvn/ .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw -B -e org.apache.maven.plugins:maven-dependency-plugin:3.1.2:go-offline

FROM eclipse-temurin:21.0.5_11-jdk-alpine AS builder

WORKDIR /application
COPY --from=dependencies /root/.m2 /root/.m2
COPY --from=dependencies /application/ /application
COPY src /application/src

RUN ./mvnw -B -e clean install -DskipTests

FROM eclipse-temurin:21.0.5_11-jdk-alpine

RUN apk --no-cache add curl

WORKDIR /application

COPY --from=builder /application/target/budgetapi-0.0.1-SNAPSHOT.jar /application/budgetapi-0.0.1-SNAPSHOT.jar
COPY --from=builder /application/opentelemetry-javaagent.jar /application/opentelemetry-javaagent.jar

RUN addgroup --system juser

RUN adduser -S -s /bin/false -G juser juser

RUN chown -R juser:juser /application

USER juser

HEALTHCHECK --interval=5s --timeout=3s CMD curl --fail http://localhost:8080/api/actuator/health || exit 1

CMD ["java", "-XX:+UseG1GC", "-javaagent:./opentelemetry-javaagent.jar", "-Dotel.instrumentation.common.default-enabled=false", "-Dotel.instrumentation.micrometer.enabled=true", "-Dotel.instrumentation.spring-boot-actuator-autoconfigure.enabled=true", "-jar", "budgetapi-0.0.1-SNAPSHOT.jar"]