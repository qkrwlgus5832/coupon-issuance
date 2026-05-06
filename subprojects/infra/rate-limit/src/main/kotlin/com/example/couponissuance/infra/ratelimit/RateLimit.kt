package com.example.couponissuance.infra.ratelimit

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(
    val key: String,
    val limit: Long,
    val window: Long,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
)
