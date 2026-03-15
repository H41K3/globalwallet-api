# Estágio 1: Build (O Cozinheiro)
# Usa uma imagem do Maven para compilar o código
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Executa o comando para gerar o .jar, pulando os testes para ser mais rápido
RUN mvn clean package -DskipTests

# Estágio 2: Run (O Garçom)
# Usa uma imagem leve do Java para rodar a aplicação
FROM eclipse-temurin:25-jdk
WORKDIR /app
# Copia o "bolo assado" do Estágio 1 para este estágio final
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]