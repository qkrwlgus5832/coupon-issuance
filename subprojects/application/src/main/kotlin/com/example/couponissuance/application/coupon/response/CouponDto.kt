package com.example.couponissuance.application.coupon.response

import java.time.LocalDateTime

data class CouponDto (
    val id: Long,
    val name: String,
    val description: String,
    val count: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)