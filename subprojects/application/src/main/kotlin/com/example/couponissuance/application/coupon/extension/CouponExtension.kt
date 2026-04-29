package com.example.couponissuance.application.coupon.extension

import com.example.couponissuance.domain.coupon.entity.Coupon
import com.example.couponissuance.application.coupon.response.CouponDto


object CouponExtension {
    fun Coupon?.toDto(): CouponDto? {
        return this?.let {
            CouponDto(
                id = this.id,
                name = this.name,
                description = this.description,
                count = this.count,
                createdAt = this.createdAt!!,
                updatedAt = this.updatedAt!!
            )
        }
    }
}