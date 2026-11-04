# syntax=docker/dockerfile:1

###########################################################################
# Build stage: compila o boot jar usando o Gradle Wrapper e JDK 21
###########################################################################
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace

# Copia primeiro os arquivos de build para aproveitar o cache de camadas:
# enquanto build.gradle.kts/wrapper nao mudam, o passo de dependencias e reusado.
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY build-logic ./build-logic
RUN chmod +x gradlew && ./gradlew --no-daemon help --quiet || true

# Compila a aplicacao (testes rodam em jobs/etapas dedicados do CI)
COPY shared ./shared
COPY features ./features
COPY api ./api
RUN ./gradlew --no-daemon :api:bootJar -x test \
    && cp api/build/libs/*.jar application.jar

# Extrai as camadas do jar do Spring Boot para otimizar o cache em runtime
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

###########################################################################
# Runtime stage: imagem enxuta apenas com o JRE 21 + New Relic Java Agent
###########################################################################
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /application

RUN groupadd --system spring && useradd --system --gid spring spring

# New Relic Java Agent (APM). License key chega via NEW_RELIC_LICENSE_KEY em runtime.
# Sem a key o agent sobe desabilitado e a aplicacao continua normal.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl unzip ca-certificates \
    && mkdir -p /newrelic \
    && curl -fsSL -o /tmp/newrelic-java.zip \
         https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip \
    && unzip -q /tmp/newrelic-java.zip -d /tmp \
    && mv /tmp/newrelic/newrelic.jar /newrelic/newrelic.jar \
    && rm -rf /tmp/newrelic /tmp/newrelic-java.zip \
    && apt-get purge -y curl unzip \
    && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/*

COPY newrelic/newrelic.yml /newrelic/newrelic.yml
RUN chown -R spring:spring /newrelic

USER spring:spring

# Cada camada e copiada separadamente (da que muda menos para a que muda mais)
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

ENV NEW_RELIC_APP_NAME=oficina-mecanica-staging

EXPOSE 8080
ENTRYPOINT [\
  "java",\
  "-javaagent:/newrelic/newrelic.jar",\
  "--enable-native-access=ALL-UNNAMED",\
  "-jar", "application.jar"\
]
