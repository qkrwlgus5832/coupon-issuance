package com.example.couponissuance.ui.api.controller.coupon

import com.example.couponissuance.application.coupon.request.CreateRequest
import com.example.couponissuance.application.coupon.request.InssuanceRequest
import com.example.couponissuance.application.coupon.response.CreateResponse
import com.example.couponissuance.application.coupon.response.InssuanceResponse
import com.example.couponissuance.application.coupon.lock.CouponOptimisticLock
import com.example.couponissuance.application.coupon.service.CouponService
import com.example.couponissuance.infra.ratelimit.RateLimit
import java.util.concurrent.TimeUnit
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coupon")
class CouponController(
    private val couponService: CouponService,
    private val couponOptimisticLock: CouponOptimisticLock,
) {

    @PostMapping("/inssuance")
    @RateLimit(
        key = "#request.userName",
        limit = 5,
        window = 1,
        timeUnit = TimeUnit.MINUTES,
    )
    fun inssuance(@RequestBody request: InssuanceRequest): InssuanceResponse {
        return couponService.inssuanceWithDistributedLock(request)
    }

    @PostMapping("/create")
    fun create(@RequestBody request: CreateRequest): CreateResponse {
        return couponService.createCoupon(request)
    }

    @PostMapping("/inssuance/redis")
    fun inssuanceWithRedisDecr(@RequestBody request: InssuanceRequest): InssuanceResponse {
        return couponService.inssuanceWithRedisDecr(request)
    }

    @GetMapping("/failedCount")
    fun getFailedCount(): Int {
        return couponOptimisticLock.failedCount
    }
}