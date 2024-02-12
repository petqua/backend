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
    implementation("ch.qos.logback:logback-access")


    // spring data jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // spring boot web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

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

    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    // test
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.4.2")
    testImplementation("io.kotest:kotest-assertions-core:5.4.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    testImplementation("io.rest-assured:rest-assured:5.3.1")
    testImplementation("io.rest-assured:kotlin-extensions:5.3.1")
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
