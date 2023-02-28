FROM openjdk:17-alpine3.14

RUN apk --no-cache add curl

WORKDIR /application

COPY ./target/budgetapi-0.0.1-SNAPSHOT.jar /application

RUN addgroup --system juser

RUN adduser -S -s /bin/false -G juser juser

RUN chown -R juser:juser /application

USER juser

HEALTHCHECK --interval=5s --timeout=3s CMD curl --fail http://localhost:8080/api/actuator/health || exit 1

CMD ["java", "-jar", "budgetapi-0.0.1-SNAPSHOT.jar"]