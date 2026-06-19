plugins {
    java
    id("io.qameta.allure") version "2.11.2"
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

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    implementation("org.seleniumhq.selenium:selenium-remote-driver:$seleniumVersion")

    implementation("org.aeonbits.owner:owner:1.0.12")

    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("io.qameta.allure:allure-java-commons:$allureVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.assertj:assertj-core:3.25.1")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.9.0")
    testImplementation("io.qameta.allure:allure-junit5:$allureVersion")

     implementation ("org.projectlombok:lombok:$lombokVersion")
     annotationProcessor ("org.projectlombok:lombok:$lombokVersion")
     testImplementation ("org.projectlombok:lombok:$lombokVersion")
     testAnnotationProcessor ("org.projectlombok:lombok:$lombokVersion")

     testImplementation ("com.github.javafaker:javafaker:1.0.2")
 }

tasks.test {
    useJUnitPlatform()
}