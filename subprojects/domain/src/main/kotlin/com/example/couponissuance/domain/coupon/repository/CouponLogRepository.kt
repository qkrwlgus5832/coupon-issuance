package com.example.couponissuance.domain.coupon.repository

import com.example.couponissuance.domain.coupon.entity.CouponLog
import org.springframework.data.jpa.repository.JpaRepository

interface CouponLogRepository: JpaRepository<CouponLog, Long>