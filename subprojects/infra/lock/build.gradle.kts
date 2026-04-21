dependencies {
    api("org.springframework.boot:spring-boot-starter-aspectj")
    api(project(":infra:redis"))
}

tasks {
    bootJar {
        enabled = false
    }
    jar {
        enabled = true
    }
}
