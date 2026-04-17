FROM gradle:8.5-jdk17 AS build
WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test --no-daemon --stacktrace

FROM eclipse-temurin:17-jre-alpine
WORKDIR /run
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 10000
ENTRYPOINT ["sh", "-c", "exec java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-10000} -jar app.jar"]