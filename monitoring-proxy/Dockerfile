# Paso 1: Construcción (Build)
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .

# FIX 1: Le damos permisos explícitos de ejecución a Gradle (Crucial para proyectos hechos en Windows)
RUN chmod +x ./gradlew

# FIX 2: Usamos --no-daemon para que Gradle no consuma toda la RAM del plan gratuito de Render
RUN ./gradlew clean bootJar -x test --no-daemon

# Paso 2: Ejecución (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /run
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:8080} -jar app.jar"]