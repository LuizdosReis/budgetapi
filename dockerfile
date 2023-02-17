FROM openjdk:17-alpine3.14

WORKDIR /application

COPY ./target/budgetapi-0.0.1-SNAPSHOT.jar /application

RUN addgroup --system juser

RUN adduser -S -s /bin/false -G juser juser

RUN chown -R juser:juser /application

USER juser

CMD ["java", "-jar", "budgetapi-0.0.1-SNAPSHOT.jar"]