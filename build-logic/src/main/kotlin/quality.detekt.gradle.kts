plugins {
    id("dev.detekt")
}

detekt {
    toolVersion = "2.0.0-alpha.3"
    config.setFrom(isolated.rootProject.projectDirectory.file("conf/detekt/detekt.yml"))
    source.setFrom(layout.projectDirectory.dir("src/main/kotlin"))
    baseline = file("detekt-baseline.xml")
    failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Warning
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    reports {
        checkstyle.required.set(true)
        html.required.set(true)
    }
}
