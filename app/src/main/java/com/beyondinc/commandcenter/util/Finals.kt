package com.beyondinc.commandcenter.util

import androidx.lifecycle.ViewModel

object Finals {

    //View Codes
    val NOT_USE_CODE : Int = 0
    val SELECT_EMPTY : Int = 1
    val SELECT_BRIFE : Int = 2
    val SELECT_STORE : Int = 3
    val SELECT_RIDER : Int = 4

    val OPEN_POPUP_BRIEF : Int = 6
    val OPEN_POPUP_STORE : Int = 7
    val OPEN_POPUP_RIDER : Int = 8
    val CLOSE_POPUP : Int = 9

    val SELECT_MAP : Int = 14
    val SELECT_ORDER : Int = 15
    val SELECT_CHECK : Int = 16

    val ClOSE_CHECK : Int = 21

    val breiftitle = "라이더 배정"
    val storetitle = "가맹점 기준 조회"
    val ridertitle = "라이더 기준 조회"

    val CALL_STORE : Int = 41
    val CALL_RIDER : Int = 42
    val CALL_ORDER : Int = 43
    val CALL_CENTER : Int = 44

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

    val CREATE_RIDER_MARKER : Int = 71
    val MAP_FOR_DOPEN : Int = 72
    val MAP_FOR_DCLOSE : Int = 73

    val MAP_FOR_ORDER : Int = 81
    val MAP_FOR_STATE : Int = 82
    val MAP_FOR_REMOVE : Int = 83
    val MAP_MOVE_FOCUS : Int = 84
    val MAP_FOR_CALL_RIDER : Int = 85
    val MAP_FOR_LIVE_DATA : Int = 86

    val LOGIN_SUCESS : Int = 101
    val LOGIN_FAIL : Int = 102

    val HTTP_ERROR : Int = 111

    val CLOSE_KEYBOARD : Int = 121
    val SET_BRIGHT : Int = 122

}