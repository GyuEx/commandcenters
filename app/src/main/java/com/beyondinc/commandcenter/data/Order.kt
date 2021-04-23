package com.beyondinc.commandcenter.repository.database.entity

import com.beyondinc.commandcenter.util.Codes

data class Order(
    // 오더 기본 정보
        val id: String,                                         // 오더 ID
        val receiptTime: Long,                                  // 오더 접수시간
        val modifyTime: Long,                                   // 오더 최종 수정시간 (최신정보 판단용)
    // 오더 접수 가맹점 정보
        val agencyID: String,                                   // 가맹점 ID
        var agencyName: String,                                 // 가맹점 이름
        val centerID: String,                                   // 가맹점 ID
        val centerName: String,                                 // 가맹점 이름
        val agencyPhone: String,                                // 가맹점 연락처
        val agencyAddress: String,                              // 가맹점 지번-도로명 주소
        val agencyAddressForMap: String,                        // 가맹점 지도 호출용 주소
        val agencyLatitude: Double,                             // 가맹점 좌표 위도값
        val agencyLongitude: Double,                            // 가맹점 좌표 경도값
    // 고객 정보
        val customerID: String,                                 // 고객 ID
        val customerName: String,                               // 고객 이름
        val customerPhoneNumber: String,                        // 고객 전화번호
        val agencyMemo: String,                                 // 전달사항
        var deliveryRegionAddress: String,                      // 배달지 주소
        var deliveryDetailAddress: String,                      // 배달지 주소
        var deliveryAddressForMap: String,                      // 배달지 지도 호출용 주소
        var deliveryLatitude: Double,                           // 배달지 좌표 위도값
        var deliveryLongitude: Double,                          // 배달지 좌표 경도값
    // 금액 관련 정보
        var salesPrice: Long,                                   // 판매금액
        var deliveryBaseFee: Long,                              // 배달 기본요금 (할증 등 요소가 포함된 초기 설정요금)
        var deliveryExtraFee: Long,                             // 배달 추가요금 (오더상세 화면에서 추가 입력한 요금)
        var paymentTypeCode: String,                            // 지불 방법 코드 (Codes.PaymentType 참고)
        var paymentTypeName: String,                            // 지불 방법 이름 (Codes.PaymentType 참고)
    // 배달거리 및 픽업 관련 정보
        var deliveryDistance: String,                           // 배달거리
        var pickupRequirementTime: Int,                         // 픽업요청시간
    // 접수시 기본 설정값
        var orderStatusCode: String =        Codes.ORDER_RECEIPT,                                // 오더 현황 코드 (Codes.OrderStatus 참고)
        var orderStatusName: String =        Codes.OrderStatus[orderStatusCode] ?: error(""),    // 오더 현황 이름 (Codes.OrderStatus 참고)
        var isPickupReady: Boolean = false,                     // 포장완료 여부 (기본값 false, true 일떄는 포장완료 상태)
    // 라이더 관련 정보
        var riderID: String = "",                               // 라이더 ID
        var riderName: String = "",                             // 라이더 이름
        var riderPhone: String = "",                            // 라이더 연락처
        var riderWorkingCount: String = "",                     // 라이더 진행건수
    // 시간 정보
        var assignTime: Long = 0L,                              // 배정시간
        var pickupTime: Long = 0L,                              // 픽업시간
        var completeTime: Long = 0L,                            // 오더완료시간
        var cancelTime: Long = 0L,                              // 오더취소시간
)
