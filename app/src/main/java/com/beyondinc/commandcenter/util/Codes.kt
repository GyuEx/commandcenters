package com.beyondinc.commandcenter.util

object Codes {
    // 오더현황 필터 관련
    const val FILTER_ALL = 0
    const val FILTER_AGENCY = 1
    const val FILTER_RIDER = 2

    // 오더상태 관련
    const val ORDER_RECEIPT = "301"
    const val ORDER_ASSIGN = "303"
    const val ORDER_PICKUP = "305"
    const val ORDER_COMPLETE = "308"
    const val ORDER_CANCEL = "401"

    val OrderStatus = mapOf(
            ORDER_RECEIPT to "접수",
            ORDER_ASSIGN to "배정",
            ORDER_PICKUP to "픽업",
            ORDER_COMPLETE to "완료",
            ORDER_CANCEL to "취소",
    )

    // 사운드 채널
    const val SOUND_ORDER_RECEIPT = 301
    const val SOUND_ORDER_ASSIGN = 303
    const val SOUND_ORDER_COMPLETE = 308
    const val SOUND_ORDER_CANCEL = 401

    // 라이더 근무상태 관련
    const val RIDER_ON_WORK = "1301"
    const val RIDER_OFF_WORK = "1302"
    const val RIDER_ON_EAT = "1303"

    // 지불방법 관련
    const val PAYMENT_MONEY = "2701"
    const val PAYMENT_CARD = "2702"
    const val PAYMENT_PREPAY = "2704"

    val PaymentType = mapOf(
            PAYMENT_MONEY to "현금",
            PAYMENT_CARD to "카드",
            PAYMENT_PREPAY to "선결제",
    )
}