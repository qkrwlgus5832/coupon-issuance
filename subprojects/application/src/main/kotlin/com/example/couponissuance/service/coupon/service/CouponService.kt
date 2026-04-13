package com.example.couponissuance.service.coupon.service

import com.example.couponissuance.domain.coupon.entity.Coupon
import com.example.couponissuance.domain.coupon.entity.CouponLog
import com.example.couponissuance.domain.coupon.repository.CouponLogRepository
import com.example.couponissuance.domain.coupon.repository.CouponRepository
import com.example.couponissuance.service.coupon.extension.CouponExtension.toDto
import com.example.couponissuance.service.coupon.request.CreateRequest
import com.example.couponissuance.service.coupon.request.InssuanceRequest
import com.example.couponissuance.service.coupon.response.CreateResponse
import com.example.couponissuance.service.coupon.response.InssuanceResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val couponLogRepository: CouponLogRepository
) {
    @Transactional
    fun inssuanceWithPessimisticLock(request: InssuanceRequest): InssuanceResponse {
        val coupon = couponRepository.findByIdWithPessimisticLock(request.couponId)

        if (coupon!!.count <= 0L) {
            throw RuntimeException("쿠폰이 모두 소진되었습니다 !")
        }

        coupon.count = coupon.count - 1
        couponLogRepository.save(CouponLog(request.couponId, request.userName))
        return InssuanceResponse(isSuccess = true, coupon = coupon.toDto())
    }

    @Transactional
    fun inssuanceWithOptimisticLock(request: InssuanceRequest): InssuanceResponse {
        val coupon = couponRepository.findById(request.couponId).get()

        if (coupon.count <= 0L) {
            throw RuntimeException("쿠폰이 모두 소진되었습니다 !")
        }

        coupon.count = coupon.count - 1
        couponLogRepository.save(CouponLog(request.couponId, request.userName))
        return InssuanceResponse(isSuccess = true, coupon = coupon.toDto())
    }

    @Transactional
    fun createCoupon(request: CreateRequest): CreateResponse {
        val coupon = couponRepository.save(
            Coupon(
                name = request.name,
                description = request.description
            ).apply {
                count = request.count.toLong()
            }
        )

        return CreateResponse(
            coupon.toDto()!!
        )
    }
}