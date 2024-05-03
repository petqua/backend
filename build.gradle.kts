import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "com"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // spring data
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // spring boot web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.2.0")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // annotation processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // kotlin jdsl
    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.3.0")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:3.3.0")

    // spring boot cache
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // logging
    implementation("ch.qos.logback:logback-access")
    implementation("org.codehaus.janino:janino:3.1.6")

    // s3
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.693")

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    testImplementation("io.kotest:kotest-runner-junit5:5.4.2")
    testImplementation("io.kotest:kotest-assertions-core:5.4.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

    testImplementation("io.rest-assured:rest-assured:5.3.1")
    testImplementation("io.rest-assured:kotlin-extensions:5.3.1")

    // testcontainers
    testImplementation("org.testcontainers:testcontainers")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// plain jar 제거
tasks.named("jar") {
    enabled = false
}

tasks.named<JavaCompile>("compileJava") {
    inputs.files(tasks.named("processResources"))
}

tasks.named<Copy>("processResources") {
    dependsOn("copySecret")
}

tasks.register("copySecret", Copy::class) {
    from("./backend-submodule")
    include("application*.yml")
    into("./src/main/resources/")
}

tasks.test {
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED"
    )
}
