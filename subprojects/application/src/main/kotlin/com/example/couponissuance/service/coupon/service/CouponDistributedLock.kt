package com.example.couponissuance.service.coupon.service

import com.example.couponissuance.domain.coupon.entity.CouponLog
import com.example.couponissuance.infra.redis.service.RedisService
import com.example.couponissuance.service.coupon.extension.CouponExtension.toDto
import com.example.couponissuance.service.coupon.request.InssuanceRequest
import com.example.couponissuance.service.coupon.response.InssuanceResponse
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CouponDistributedLock(
    private val redisService: RedisService,
    private val couponService: CouponService
) {

    fun inssuanceDistributedLock(request: InssuanceRequest): InssuanceResponse {
        val lock = redisService.getLock("coupon:lock:${request.couponId}")
        try {
            if (!lock.tryLock(120, 30, TimeUnit.SECONDS)) {
                throw RuntimeException("락 획득 실패")
            }

            return couponService.inssuanceWithDistributedLock(request)
        } finally {
            if (lock.isHeldByCurrentThread) lock.unlock()
        }
    }

}