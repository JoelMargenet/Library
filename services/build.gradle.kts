plugins {
    id("java")
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-text:1.9")
    implementation(project(":utilities"))
    implementation(project(":domain"))
    implementation(project(":domain-implementations:jdbc"))
    implementation(project(":domain-implementations:jpa"))
    implementation(project(":cryptoutils"))
    implementation("de.vandermeer:asciitable:0.3.2")
    implementation("com.athaydes.rawhttp:rawhttp-core:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.0")


    // JPA and Hibernate dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.7.2")
    implementation("mysql:mysql-connector-java:8.0.30")
    implementation("jakarta.persistence:jakarta.persistence-api:3.0.0")
    implementation("org.hibernate:hibernate-core:5.6.10.Final")
}

application {
    mainClass.set("com.library.services.App")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
