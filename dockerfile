FROM maven:3.8.6-openjdk-18 AS build_step

RUN mkdir /project

COPY . /project

WORKDIR /project

RUN mvn clean package -DskipTests

FROM openjdk:17-alpine3.14

WORKDIR /application

COPY --from=build_step /project/target/budgetapi-0.0.1-SNAPSHOT.jar /application

RUN addgroup --system juser

RUN adduser -S -s /bin/false -G juser juser

RUN chown -R juser:juser /application

USER juser

CMD ["java", "-jar", "budgetapi-0.0.1-SNAPSHOT.jar"]