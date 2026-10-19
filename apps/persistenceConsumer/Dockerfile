# Imagem do persistenceConsumer (build a partir do código do repositório)
# Configuração só por variáveis de ambiente: DB_URL, DB_USER, DB_PASS, ACTIVEMQ_URL, ACTIVEMQ_USER, ACTIVEMQ_PASS

# Etapa 1: compila o jar
FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

# Dependências primeiro: só baixa de novo quando o pom.xml mudar (cache do Docker)
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
RUN ./mvnw -B -q package -DskipTests

# Etapa 2: imagem final só com o Java de execução e o jar
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --no-create-home app
COPY --from=build /build/target/*.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
