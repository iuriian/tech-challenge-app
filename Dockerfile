# syntax=docker/dockerfile:1

###########################################################################
# Build stage: compila o boot jar usando o Gradle Wrapper e JDK 21
###########################################################################
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Copia primeiro os arquivos de build para aproveitar o cache de camadas:
# enquanto build.gradle.kts/wrapper nao mudam, o passo de dependencias e reusado.
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies --quiet || true

# Compila a aplicacao (testes rodam em jobs/etapas dedicados do CI)
COPY src ./src
COPY features ./features
RUN ./gradlew --no-daemon clean bootJar -x test \
    && cp build/libs/*.jar application.jar

# Extrai as camadas do jar do Spring Boot para otimizar o cache em runtime
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

###########################################################################
# Runtime stage: imagem enxuta apenas com o JRE 21
###########################################################################
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /application

# Executa como usuario sem privilegios
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

# Cada camada e copiada separadamente (da que muda menos para a que muda mais)
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

EXPOSE 8080
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "application.jar"]