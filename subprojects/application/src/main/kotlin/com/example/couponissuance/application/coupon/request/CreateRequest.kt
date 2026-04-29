package com.example.couponissuance.application.coupon.request

data class CreateRequest (
    val name: String,
    val count: Int,
    val description: String
)