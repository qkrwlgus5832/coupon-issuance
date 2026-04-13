package com.example.couponissuance.ui.api.controller.coupon

import com.example.couponissuance.service.coupon.request.CreateRequest
import com.example.couponissuance.service.coupon.request.InssuanceRequest
import com.example.couponissuance.service.coupon.response.CreateResponse
import com.example.couponissuance.service.coupon.response.InssuanceResponse
import com.example.couponissuance.service.coupon.service.CouponOptimisticLock
import com.example.couponissuance.service.coupon.service.CouponService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coupon")
class CouponController(
    private val couponService: CouponService,
    private val couponOptimisticLock: CouponOptimisticLock
) {

    @PostMapping("/inssuance")
    fun inssuance(@RequestBody request: InssuanceRequest): InssuanceResponse {
        return couponOptimisticLock.inssuanceWithOptimisticLock(request).apply {
            System.out.print(couponOptimisticLock.failedCount)
        }
    }

    @PostMapping("/create")
    fun create(@RequestBody request: CreateRequest): CreateResponse {
        return couponService.createCoupon(request)
    }
}