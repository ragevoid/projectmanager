FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=build /app/target/projectmanager-3.3.3-SNAPSHOT.jar app.jar

EXPOSE 3333

ENTRYPOINT ["java", "-jar", "app.jar"]