plugins {
    id("app.kotlin-application")
    id("quality.spotless")
    id("quality.detekt")
    id("quality.jacoco")
}

val openapiVersion = "2.8.5"
val logstashLogbackVersion = "8.1"
val newRelicApiVersion = "9.5.0"

// O modulo api exige cobertura acima da linha de base definida em quality.jacoco.
tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.95".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.95".toBigDecimal()
            }
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.95".toBigDecimal()
            }
        }
    }
}

dependencies {
    // Shared kernel: dominio comum aos modulos de feature
    implementation(project(":shared"))

    // Modulos de feature compostos por esta aplicacao
    implementation(project(":features:funcionario"))
    implementation(project(":features:peca"))
    implementation(project(":features:cliente"))
    implementation(project(":features:veiculo"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openapiVersion")

    // Migracoes versionadas em src/main/resources/db/migration
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Logs JSON estruturados (stdout) + API do agent New Relic (correlacao de traces)
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackVersion")
    implementation("com.newrelic.agent.java:newrelic-api:$newRelicApiVersion")

    "developmentOnly"("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.security:spring-security-test")
}
