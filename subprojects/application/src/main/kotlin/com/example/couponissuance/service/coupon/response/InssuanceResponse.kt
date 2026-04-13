package com.example.couponissuance.service.coupon.response

import com.example.couponissuance.domain.coupon.entity.Coupon

data class InssuanceResponse(
    val isSuccess: Boolean,
    val coupon: CouponDto?
)