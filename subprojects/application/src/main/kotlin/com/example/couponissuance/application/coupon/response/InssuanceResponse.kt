package com.example.couponissuance.application.coupon.response

data class InssuanceResponse(
    val isSuccess: Boolean,
    val coupon: CouponDto?
)