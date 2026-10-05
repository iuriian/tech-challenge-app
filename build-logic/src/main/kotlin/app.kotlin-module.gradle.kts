import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.jpa")
    id("io.spring.dependency-management")
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

repositories { mavenCentral() }

extensions.configure<DependencyManagementExtension> {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

dependencies {
    "testImplementation"("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }
    "testImplementation"("io.mockk:mockk-jvm:1.14.11")
    "testImplementation"("com.ninja-squad:springmockk:4.0.2")
    "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
    "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")

    "testImplementation"("org.springframework.boot:spring-boot-testcontainers")
    "testImplementation"("org.testcontainers:junit-jupiter")
    "testImplementation"("org.testcontainers:postgresql")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    val dockerApiVersion = System.getenv("API_VERSION") ?: "1.44"
    environment("API_VERSION", dockerApiVersion)
    jvmArgs("-Dapi.version=$dockerApiVersion")
}
