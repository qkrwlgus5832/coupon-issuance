package com.example.couponissuance.ui.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.example.couponissuance"])
@Configuration
@EntityScan(basePackages = ["com.example.couponissuance.domain"])
@EnableJpaRepositories(basePackages = ["com.example.couponissuance.domain"])
open class CouponIssuanceApplication

fun main(args: Array<String>) {
    runApplication<CouponIssuanceApplication>(*args)
}
