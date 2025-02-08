FROM eclipse-temurin:17 AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y git && apt-get clean

COPY . .

RUN chmod +x ./gradlew && ./gradlew clean build -x test

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
