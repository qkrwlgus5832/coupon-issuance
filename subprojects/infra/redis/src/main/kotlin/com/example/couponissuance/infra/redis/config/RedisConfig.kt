package com.example.couponissuance.infra.redis.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.config.ReadMode
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.cluster.nodes}") private val nodes: String,
) {

    @Bean(destroyMethod = "shutdown")
    fun redissonClient(): RedissonClient {
        val config = Config()
        val clusterConfig = config.useClusterServers()
            .setReadMode(ReadMode.MASTER_SLAVE)
            .setScanInterval(2000)
            .setRetryAttempts(3)
            .setRetryInterval(1500)
            .setTimeout(3000)

        nodes.split(",").forEach { node ->
            clusterConfig.addNodeAddress("redis://${node.trim()}")
        }

        return Redisson.create(config)
    }
}
