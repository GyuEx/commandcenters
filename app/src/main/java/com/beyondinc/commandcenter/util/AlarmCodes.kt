package com.beyondinc.commandcenter.util

object AlarmCodes {

    // 알림형태(오더상태/예치금상태/공지사항)
    const val ALARM_TYPE_ORDER = 4901 // 오더 상태 알림
    const val ALARM_ORDER_STATE_301 = 301 // 접수?
    const val ALARM_ORDER_STATE_303 = 303
    const val ALARM_ORDER_STATE_305 = 305
    const val ALARM_ORDER_STATE_308 = 308
    const val ALARM_ORDER_STATE_401 = 401

    const val ALARM_TYPE_DEPOSIT = 4902 // 예치금 상태 알림
    const val ALARM_DEPOSIT_STATE_1 = 1 // 가맹점 예치금 입금
    const val ALARM_DEPOSIT_STATE_2 = 2 // 가맹점 예치금 차감
    const val ALARM_DEPOSIT_STATE_3 = 3 // 센터 예치금 입금
    const val ALARM_DEPOSIT_STATE_4 = 4 // 센터 예치금 차감


    const val ALARM_TYPE_NOTICE = 4903 // 공지사항 알림
    const val ALARM_NOTICE_STATE_0 = 0 // 전체 공지
    const val ALARM_NOTICE_STATE_1701 = 1701 // 관제센터
    const val ALARM_NOTICE_STATE_1702 = 1702 // 기사
    const val ALARM_NOTICE_STATE_1703 = 1703 // 가맹점


    const val ALARM_TYPE_ORDER_WORKENV = 4904 // 오더 작업환경 변경
    const val ALARM_ORDER_WORKENV_SURCHARGE = 1 // 할증적용 상태
    const val ALARM_ORDER_WORKENV_DELAY = 2 // 배달지연
    const val ALARM_ORDER_WORKENV_WORKSTATE = 3 // 센터 업무상태(정상업무,일시정지 업무종료)


    const val ALARM_TYPE_MESSAGE = 4905 // 쪽지 알림
    const val ALARM_MESSAGE_MANAGER2AGENCY = 4801 // 관리자->가맹점 쪽지
    const val ALARM_MESSAGE_MANAGER2RIDER = 4802 // 관리자->라이더 쪽지
    const val ALARM_MESSAGE_RIDER2AGENCY = 4803 // 라이더->가맹점 쪽지
    const val ALARM_MESSAGE_AGENCY2RIDER = 4805 // 가맹점->라이더 쪽지


    const val ALARM_TYPE_RIDER_WORK_STATE = 4906 // 라이더 상태 알림
    // 1:출근 2:퇴근 3:식사 시작 4:식사 끝 5:라이더 환경설정(배정개수 제한, 신규목록 개수 제한 등), 6:라이더 오더 제한
    const val RIDER_WORK_STATE_ON_WORK = 1
    const val RIDER_WORK_STATE_OFF_WORK = 2
    const val RIDER_WORK_STATE_START_EAT_TIME = 3
    const val RIDER_WORK_STATE_END_EAT_TIME = 4

    const val ALARM_TYPE_CHATTING = 4907 // 채팅 알림
}