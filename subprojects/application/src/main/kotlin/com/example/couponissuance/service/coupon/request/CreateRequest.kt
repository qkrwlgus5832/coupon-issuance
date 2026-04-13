package com.example.couponissuance.service.coupon.request

data class CreateRequest (
    val name: String,
    val count: Int,
    val description: String
)