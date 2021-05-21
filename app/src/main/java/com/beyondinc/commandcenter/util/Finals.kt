package com.beyondinc.commandcenter.util

import com.beyondinc.commandcenter.R

object Finals {

    //View Codes
    val NOT_USE_CODE : Int = 0
    val SELECT_EMPTY : Int = 1
    val SELECT_BRIFE : Int = 2
    val SELECT_STORE : Int = 3
    val SELECT_RIDER : Int = 4

    val CHECK_TIME : Int = 6
    val CHECK_COUNT : Int = 7
    val CLOSE_DETAIL : Int = 8
    val CLOSE_POPUP : Int = 9
    val CLOSE_DIALOG : Int = 10

    val SELECT_MAP : Int = 14
    val SELECT_ORDER : Int = 15
    val SELECT_CHECK : Int = 16

    val DETAIL_MAP : Int = 17
    val DETAIL_DETAIL : Int = 18

    val CLOSE_CHECK : Int = 21
    val SUCCESS_MESSAGE : Int = 22
    val CANCEL_MESSAGE : Int = 23

    val ORDER_ASSIGN : Int = 26
    val ORDER_TOAST_SHOW : Int = 27
    val ORDER_DETAIL_CLOSE : Int = 28
    val ORDER_ASSIGN_LIST : Int = 29

    val CALL_STORE : Int = 41
    val CALL_RIDER : Int = 42
    val CALL_ORDER : Int = 43
    val CALL_CENTER : Int = 44
    val CALL_GPS : Int = 45

    val INSERT_LOGIN : Int = 51
    val INSERT_STORE : Int = 52
    val INSERT_RIDER : Int = 53
    val INSERT_ORDER : Int = 54

    val INSERT_ORDER_COUNT : Int = 55
    val INSERT_RIDER_COUNT : Int = 56

    val ORDER_ITEM_SELECT : Int = 61
    val BRIEF_ITEM_SELECT : Int = 62
    val STORE_ITEM_SELECT : Int = 63
    val RIDER_ITEM_SELECT : Int = 64
    val DESABLE_SELECT : Int = 65

    val CREATE_RIDER_MARKER : Int = 71
    val UPDATE_RIDER_MARKER : Int = 72
    val REMOVE_RIDER_MARKER : Int = 73
    val MAP_FOR_DOPEN : Int = 74
    val MAP_FOR_DCLOSE : Int = 75
    val MAP_FOR_SOPEN : Int = 76

    val MAP_FOR_ORDER : Int = 81
    val MAP_FOR_STATE : Int = 82
    val MAP_FOR_REMOVE : Int = 83
    val MAP_MOVE_FOCUS : Int = 84
    val MAP_FOR_CALL_RIDER : Int = 85
    val MAP_FOR_ITEM_SIZE : Int = 86
    val MAP_FOR_REFRASH : Int = 87
    val MAP_FOR_ASSIGN_CREATE : Int = 88
    val MAP_FOR_ASSIGN_REMOVE : Int = 89

    val SEND_ITEM : Int = 91
    val CHANGE_ADDRESS : Int = 92
    val CHANGE_PAYMENT : Int = 93
    val CHANGE_CLOSE : Int = 94

    val LOGIN_SUCESS : Int = 101
    val LOGIN_FAIL : Int = 102

    val HTTP_ERROR : Int = 111
    val SEND_TELEPHONE : Int = 112

    val CLOSE_KEYBOARD : Int = 121
    val SET_BRIGHT : Int = 122

    val APK_UPDATE : Int = 131
    val APK_INSTALL : Int = 132
    val DOWNLOADVALUE : Int = 133

    val SHOW_LOADING : Int = 141
    val CLOSE_LOADING : Int = 142
    val CONN_ALRAM : Int = 143
    val DISCONN_ALRAM : Int = 144
    val SHOW_MESSAGE : Int = 145

    val INSERT_ADDR : Int = 151
    val SEARCH_ADDR : Int = 152
    val CHANGE_ADDR : Int = 153

    const val REQUEST_PERMISSION = 10001
    const val KAKAO_KEY = "c5a3395640f428b7cd90ea429d902c8d"
    val soundArray = arrayOf(R.raw.alarm_reg_order,R.raw.alarm_order_rider,R.raw.alarm_order_finish,R.raw.alarm_order_cancel) //픽업이없네?

}