plugins {
    id("app.kotlin-module")
    id("org.springframework.boot")
}

tasks.named<Jar>("jar") { enabled = false }
tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
