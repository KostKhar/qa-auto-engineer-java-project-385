plugins {
    java
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
val chromeVersion = "149"
val allureVersion = "2.35.2"
val lombokVersion = "1.18.38"
val junit5 = "5.10.3"
val faker = "1.0.2"
val owner = "1.0.12"
val assertj = "3.27.7"
val webManager = "5.9.0"

dependencies {
    annotationProcessor ("org.projectlombok:lombok:$lombokVersion")

    implementation ("org.projectlombok:lombok:$lombokVersion")
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    implementation("org.seleniumhq.selenium:selenium-remote-driver:$seleniumVersion")

    implementation("org.aeonbits.owner:owner:$owner")
    implementation("io.qameta.allure:allure-java-commons:$allureVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junit5")
    testImplementation("org.assertj:assertj-core:$assertj")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webManager")
    testImplementation("io.qameta.allure:allure-junit5:$allureVersion")
    testImplementation ("com.github.javafaker:javafaker:$faker")

    testImplementation ("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor ("org.projectlombok:lombok:$lombokVersion")
 }

tasks.test {
    useJUnitPlatform()
    val appBaseUrl = findProperty("APP_BASE_URL")?.toString()
        ?: System.getenv("APP_BASE_URL")
    if (!appBaseUrl.isNullOrBlank()) {
        systemProperty("APP_BASE_URL", appBaseUrl)
    }
}


sonar {
  properties {
    property("sonar.projectKey", "KostKhar_qa-auto-engineer-java-project-385")
    property("sonar.organization", "kostkhar")
  }
}

checkstyle {
    configProperties["org.checkstyle.google.suppressionfilter.config"] =
        "${project.rootDir}/config/checkstyle/checkstyle-suppressions.xml"
}
