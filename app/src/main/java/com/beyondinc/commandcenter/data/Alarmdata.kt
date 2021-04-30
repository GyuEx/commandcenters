package com.beyondinc.commandcenter.data

class Alarmdata(nTotalCnt: Int,
                nSeq: Int,
                nAlarmId: Int,
                nAlarmType: Int,
                nOrderId: Int,
                nDeliveryState: Int,
                nSelfAssign: Int,
                sServerTime: String?,
                sLaunchingTime: String?) {

    var mnTotalCnt = nTotalCnt
    var mnSeq = nSeq
    var mnAlarmId = nAlarmId
    var mnAlarmType = nAlarmType
    var mnOrderId = nOrderId
    var mnDeliveryState = nDeliveryState
    var mnSelfAssign = nSelfAssign
    var msServerTime = sServerTime
    var msLaunchingTime = sLaunchingTime
}