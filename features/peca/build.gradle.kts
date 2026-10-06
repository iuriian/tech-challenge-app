plugins {
    id("app.kotlin-module")
    id("quality.spotless")
    id("quality.detekt")
    id("quality.jacoco")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly("org.postgresql:postgresql")
}
