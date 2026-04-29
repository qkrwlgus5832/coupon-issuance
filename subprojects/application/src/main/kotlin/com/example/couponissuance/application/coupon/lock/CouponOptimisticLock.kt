package com.example.couponissuance.application.coupon.lock

import com.example.couponissuance.application.coupon.request.InssuanceRequest
import com.example.couponissuance.application.coupon.response.InssuanceResponse
import com.example.couponissuance.application.coupon.service.CouponService
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
                    failedCount++
                    throw RuntimeException("재시도 횟수 초과")
                }

                Thread.sleep(50)
            }
        }
    }
}