package com.example.couponissuance.service.coupon.service

import com.example.couponissuance.service.coupon.request.InssuanceRequest
import com.example.couponissuance.service.coupon.response.InssuanceResponse
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service

@Service
class CouponOptimisticLock(
    private val couponService: CouponService,
    public var failedCount: Int = 0,
) {
    fun inssuanceWithOptimisticLock(request: InssuanceRequest): InssuanceResponse {
        var retryCount = 0
        while (true) {
            try {
                return couponService.inssuanceWithOptimisticLock(request)
            } catch (e: OptimisticLockingFailureException) {
                if (retryCount++ >= 3) {
                    throw RuntimeException("재시도 횟수 초과")
                    failedCount++
                }
            }
        }
    }
}