plugins {
    id("java")
    id("application")
}

group = "dev.spence"
version = "1.0"

application {
    mainClass = "dev.spence.Server"
}

repositories {
    mavenCentral()
}

dependencies {
    // Junit
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Mockito
    testImplementation("org.mockito:mockito-core:3.+")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    // Slf4j
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks.test {
    useJUnitPlatform()
}
