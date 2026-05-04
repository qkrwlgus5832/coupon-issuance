package com.example.couponissuance.application.coupon.service

import com.example.couponissuance.domain.coupon.entity.Coupon
import com.example.couponissuance.domain.coupon.entity.CouponLog
import com.example.couponissuance.domain.coupon.repository.CouponLogRepository
import com.example.couponissuance.domain.coupon.repository.CouponRepository
import com.example.couponissuance.infra.lock.DistributedLock
import com.example.couponissuance.infra.redis.service.RedisService
import com.example.couponissuance.application.coupon.extension.CouponExtension.toDto
import com.example.couponissuance.application.coupon.request.CreateRequest
import com.example.couponissuance.application.coupon.request.InssuanceRequest
import com.example.couponissuance.application.coupon.response.CreateResponse
import com.example.couponissuance.application.coupon.response.InssuanceResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val couponLogRepository: CouponLogRepository,
    private val redisService: RedisService
) {
    companion object {
        const val COUPON_ISSUANCE = "COUPON_ISSUANCE"
        const val COUPON_STOCK_KEY = "coupon:stock:"
    }

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

    @DistributedLock(key = "'coupon:lock:' + #request.couponId")
    @Transactional
    fun inssuanceWithDistributedLock(request: InssuanceRequest): InssuanceResponse {
        val coupon = couponRepository.findById(request.couponId).get()
        if (coupon.count <= 0L) throw RuntimeException("쿠폰이 모두 소진되었습니다 !")
        coupon.count = coupon.count - 1
        couponLogRepository.save(CouponLog(request.couponId, request.userName))
        return InssuanceResponse(isSuccess = true, coupon = coupon.toDto())
    }

    @Transactional
    fun inssuanceWithRedisDecr(request: InssuanceRequest): InssuanceResponse {
        val stock = redisService.decrementStock("$COUPON_STOCK_KEY${request.couponId}")
        if (stock == -1L) {
            throw RuntimeException("쿠폰이 모두 소진되었습니다 !")
        }
        couponRepository.decrementCount(request.couponId)
        val coupon = couponRepository.findById(request.couponId).get()
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
        redisService.setStock("$COUPON_STOCK_KEY${coupon.id}", request.count.toLong())
        return CreateResponse(coupon.toDto()!!)
    }
}