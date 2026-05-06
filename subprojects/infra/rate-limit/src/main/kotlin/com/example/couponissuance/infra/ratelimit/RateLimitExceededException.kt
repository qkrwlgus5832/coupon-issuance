package com.example.couponissuance.infra.ratelimit

class RateLimitExceededException(
    val rateLimitKey: String,
    val limit: Long,
    val windowMillis: Long,
) : RuntimeException("Rate limit exceeded: key=$rateLimitKey, limit=$limit per ${windowMillis}ms")
