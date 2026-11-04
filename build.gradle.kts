plugins {
    base
    id("jacoco-report-aggregation")
}

group = "br.com.fiap.oficina"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    jacocoAggregation(project(":api"))
}

