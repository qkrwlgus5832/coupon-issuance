package com.example.couponissuance.infra.lock

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(
    val key: String,
    val waitTime: Long = 20,
    val leaseTime: Long = 30,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
)
