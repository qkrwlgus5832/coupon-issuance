package com.example.couponissuance.infra.redis.service

import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redissonClient: RedissonClient,
) {

    fun add(key: String, value: String, ttl: Long, timeUnit: TimeUnit = TimeUnit.SECONDS) {
        redissonClient.getBucket<String>(key).set(value, ttl, timeUnit)
    }

    fun get(key: String): String? =
        redissonClient.getBucket<String>(key).get()

    fun delete(key: String): Boolean =
        redissonClient.getBucket<String>(key).delete()

    fun getLock(key: String): RLock =
        redissonClient.getLock(key)
}
