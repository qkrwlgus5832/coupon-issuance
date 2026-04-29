dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    api(project(":application"))
    testImplementation("com.tngtech.archunit:archunit:1.1.0")
}
