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

FROM eclipse-temurin:21.0.5_11-jdk-alpine AS extractor
WORKDIR /application

COPY --from=builder /application/target/budgetapi-0.0.1-SNAPSHOT.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-jammy

WORKDIR /application

RUN useradd -ms /bin/false juser
USER juser

COPY --chown=juser:juser --from=downloader /opentelemetry-javaagent.jar /application/opentelemetry-javaagent.jar

COPY --chown=juser:juser --from=extractor /application/dependencies/ ./
COPY --chown=juser:juser --from=extractor /application/spring-boot-loader/ ./
COPY --chown=juser:juser --from=extractor /application/snapshot-dependencies/ ./
COPY --chown=juser:juser --from=extractor /application/application/ ./

ENTRYPOINT java -Dotel.semconv-stability.opt-in=database -javaagent:./opentelemetry-javaagent.jar org.springframework.boot.loader.launch.JarLauncher "$@"