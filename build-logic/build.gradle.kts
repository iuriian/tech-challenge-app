import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins { `kotlin-dsl` }

repositories {
    gradlePluginPortal()
    mavenCentral()
}

val kotlinVersion = "2.3.21"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")

    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.4.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.6")

    implementation("dev.detekt:dev.detekt.gradle.plugin:2.0.0-alpha.3")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.2.0")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
}
