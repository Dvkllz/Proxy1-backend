# Paso 1: Construcción (Build)
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
# Usamos 'clean bootJar' para garantizar que Spring Boot genere un único JAR ejecutable
RUN ./gradlew clean bootJar -x test

# Paso 2: Ejecución (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /run
# Copiamos el JAR generado asegurando la ruta exacta
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

# Usamos 'sh -c' para que las variables de entorno de Render se lean correctamente
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]