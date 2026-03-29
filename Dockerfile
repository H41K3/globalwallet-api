# Estágio 1: Build (Compilação)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# 1. Primeiro copiamos TODO o código para dentro do Docker
COPY . .

# 2. Agora damos permissão e mandamos o Maven construir o JAR lá dentro
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# Estágio 2: Run (Execução)
FROM eclipse-temurin:25-jdk
WORKDIR /app

# 3. Agora sim, pegamos o JAR que foi criado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 4. Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]