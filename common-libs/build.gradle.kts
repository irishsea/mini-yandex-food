plugins {
    id("java-library")
}

version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // BOM Spring Boot
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.0"))
    annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:4.1.0"))
    testImplementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.0"))

    // Валидация
    api("jakarta.validation:jakarta.validation-api")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Lombok
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.test {
    useJUnitPlatform()
}