FROM alpine:3.14 AS downloader

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.13.0/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

FROM eclipse-temurin:21.0.5_11-jdk-alpine AS dependencies

WORKDIR /application

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

FROM eclipse-temurin:21.0.7_6-jre-alpine-3.21

WORKDIR /application

RUN addgroup --system juser

RUN adduser -S -s /bin/false -G juser juser

USER juser

COPY --chown=juser:juser --from=builder /application/target/budgetapi-0.0.1-SNAPSHOT.jar /application/budgetapi-0.0.1-SNAPSHOT.jar
COPY --chown=juser:juser --from=downloader /opentelemetry-javaagent.jar /application/opentelemetry-javaagent.jar

HEALTHCHECK --interval=5s --timeout=3s CMD curl --fail http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT java $JAVA_OPTS -Dotel.semconv-stability.opt-in=database -javaagent:./opentelemetry-javaagent.jar -jar budgetapi-0.0.1-SNAPSHOT.jar "$@"