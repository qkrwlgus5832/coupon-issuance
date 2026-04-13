package com.example.couponissuance.domain.coupon.repository

import com.example.couponissuance.domain.coupon.entity.Coupon
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints

interface CouponRepository: JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :id")
    fun findByIdWithPessimisticLock(id: Long): Coupon?
}