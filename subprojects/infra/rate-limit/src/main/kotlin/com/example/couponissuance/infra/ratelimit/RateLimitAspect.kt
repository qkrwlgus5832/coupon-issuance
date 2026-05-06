package com.example.couponissuance.infra.ratelimit

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RScript
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.springframework.core.annotation.Order
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.util.UUID

@Aspect
@Component
@Order(0)
class RateLimitAspect(
    private val redissonClient: RedissonClient,
) {

    private val parser = SpelExpressionParser()

    private val script = """
        local key = KEYS[1]
        local now = tonumber(ARGV[1])
        local window = tonumber(ARGV[2])
        local limit = tonumber(ARGV[3])
        local member = ARGV[4]

        redis.call('ZREMRANGEBYSCORE', key, 0, now - window)
        local count = redis.call('ZCARD', key)

        if count < limit then
            redis.call('ZADD', key, now, member)
            redis.call('PEXPIRE', key, window)
            return 1
        else
            return 0
        end
    """.trimIndent()

    @Around("@annotation(rateLimit)")
    fun around(joinPoint: ProceedingJoinPoint, rateLimit: RateLimit): Any? {
        val key = "rate-limit:" + resolveKey(joinPoint, rateLimit.key)
        val windowMillis = rateLimit.timeUnit.toMillis(rateLimit.window)
        val now = System.currentTimeMillis()
        val member = "$now:${UUID.randomUUID()}"

        val acquired: Long? = redissonClient.getScript(StringCodec.INSTANCE).eval(
            RScript.Mode.READ_WRITE,
            script,
            RScript.ReturnType.LONG,
            listOf<Any>(key),
            now.toString(), windowMillis.toString(), rateLimit.limit.toString(), member,
        )

        if (acquired != 1L) {
            throw RateLimitExceededException(key, rateLimit.limit, windowMillis)
        }

        return joinPoint.proceed()
    }

    private fun resolveKey(joinPoint: ProceedingJoinPoint, keyExpression: String): String {
        val signature = joinPoint.signature as MethodSignature
        val paramNames = signature.parameterNames
        val args = joinPoint.args

        val context = StandardEvaluationContext()
        paramNames.forEachIndexed { i, name -> context.setVariable(name, args[i]) }

        return parser.parseExpression(keyExpression).getValue(context, String::class.java)
            ?: throw IllegalArgumentException("Rate limit key 파싱 실패: $keyExpression")
    }
}
