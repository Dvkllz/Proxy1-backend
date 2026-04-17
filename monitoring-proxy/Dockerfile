# Paso 1: Construcción (Build)
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .

# FIX 1: Permisos (ESTO DEBE IR PRIMERO)
RUN chmod +x ./gradlew

# FIX 2: Compilar con no-daemon (ÚNICO COMANDO DE BUILD)
RUN ./gradlew clean bootJar -x test --no-daemon

# Paso 2: Ejecución (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /run
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:8080} -jar app.jar"]