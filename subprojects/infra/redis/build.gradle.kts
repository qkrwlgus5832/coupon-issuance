dependencies {
    api("org.redisson:redisson-spring-boot-starter:4.3.1")
}

tasks {
    bootJar {
        enabled = false
    }
    jar {
        enabled = true
    }
}
