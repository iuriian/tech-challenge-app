import org.gradle.kotlin.dsl.jacoco

plugins { jacoco }

jacoco { toolVersion = "0.8.12" }

val coverageExclusions =
    listOf(
        "**/OfficinaApplication*",
        "**/config/**",
        "**/dto/**",
        "**/*Config*",
    )

tasks.withType<JacocoReportBase>().configureEach {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(coverageExclusions)
                }
            },
        ),
    )
}

val jacocoTestReport =
    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.named<Test>("test"))
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

tasks.named<Test>("test") {
    finalizedBy(jacocoTestReport)
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(jacocoTestReport)
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}
