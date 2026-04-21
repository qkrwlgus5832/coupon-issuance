package com.example.couponissuance.infra.lock

import com.example.couponissuance.infra.redis.service.RedisService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributedLockAspect(
    private val redisService: RedisService,
) {

    private val parser = SpelExpressionParser()

    @Around("@annotation(distributedLock)")
    fun around(joinPoint: ProceedingJoinPoint, distributedLock: DistributedLock): Any? {
        val key = resolveKey(joinPoint, distributedLock.key)
        val lock = redisService.getLock(key)

        try {
            if (!lock.tryLock(distributedLock.waitTime, distributedLock.leaseTime, distributedLock.timeUnit)) {
                throw RuntimeException("락 획득 실패: $key")
            }
            return joinPoint.proceed()
        } finally {
            if (lock.isHeldByCurrentThread) lock.unlock()
        }
    }

    private fun resolveKey(joinPoint: ProceedingJoinPoint, keyExpression: String): String {
        val signature = joinPoint.signature as MethodSignature
        val paramNames = signature.parameterNames
        val args = joinPoint.args

        val context = StandardEvaluationContext()
        paramNames.forEachIndexed { i, name -> context.setVariable(name, args[i]) }

        return parser.parseExpression(keyExpression).getValue(context, String::class.java)
            ?: throw IllegalArgumentException("락 키 파싱 실패: $keyExpression")
    }
}
