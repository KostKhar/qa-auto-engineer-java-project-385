plugins {
    java
    jacoco
    id("io.qameta.allure") version "2.11.2"
    id("org.sonarqube") version "7.3.1.8318"
    checkstyle
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
val seleniumVersion = "4.40.0"
val allureVersion = "2.35.2"
val lombokVersion = "1.18.38"
val junit5 = "5.10.3"
val faker = "1.0.2"
val owner = "1.0.12"
val webManager = "5.9.0"

dependencies {
    annotationProcessor ("org.projectlombok:lombok:$lombokVersion")

    implementation ("org.projectlombok:lombok:$lombokVersion")
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")

    implementation("org.aeonbits.owner:owner:$owner")
    implementation("io.qameta.allure:allure-java-commons:$allureVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junit5")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webManager")
    testImplementation("io.qameta.allure:allure-junit5:$allureVersion")
    testImplementation ("com.github.javafaker:javafaker:$faker")

    testImplementation ("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor ("org.projectlombok:lombok:$lombokVersion")
 }

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    val appBaseUrl = providers.environmentVariable("APP_BASE_URL")
        .orElse(providers.systemProperty("APP_BASE_URL"))
    if (appBaseUrl.isPresent && appBaseUrl.get().isNotBlank()) {
        environment("APP_BASE_URL", appBaseUrl.get())
        systemProperty("APP_BASE_URL", appBaseUrl.get())
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport)
}

sonar {
    properties {
        property("sonar.projectKey", "KostKhar_qa-auto-engineer-java-project-385")
        property("sonar.organization", "kostkhar")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.absolutePath
        )
    }
}

checkstyle {
    configProperties["org.checkstyle.google.suppressionfilter.config"] =
        "${project.rootDir}/config/checkstyle/checkstyle-suppressions.xml"
}
