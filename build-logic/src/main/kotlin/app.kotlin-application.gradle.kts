plugins {
    id("app.kotlin-module")
    id("org.springframework.boot")
}

// O plugin do Boot desabilita a task `jar` simples: modulos com esta convencao
// sao executaveis (bootJar/bootRun) e nao devem ser consumidos como biblioteca.
tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
