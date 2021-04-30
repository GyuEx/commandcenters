package com.beyondinc.commandcenter.net

import android.util.Log
import com.beyondinc.commandcenter.data.Alarmdata
import com.vasone.deliveryalarm.DAClient
import java.util.*

class DACallerInterface(
    private val appID: String,
    private val loggedID: String,
    private val deliveryAlarmUrl: String,
    private val timeoutConnect: Int,
    private val timeoutRead: Int,
    private val alarmCallback: (ArrayList<Alarmdata>) -> Unit?,
) : DAClient.CallerInterface {

    private val receivedAlarmInfoList: ArrayList<Alarmdata> = arrayListOf()

    override fun GetServerAddress(): String {
        return deliveryAlarmUrl
    }

    override fun GetConnectTimeout(): Int {
        return timeoutConnect
    }

    override fun GetRecvTimeout(): Int {
        return timeoutRead
    }

    override fun GetAppId(): String {
        return appID
    }

    override fun GetLoginId(): String {
        return loggedID
    }

    override fun AlarmReceived(
            nTotalCnt: Int,
            nSeq: Int,
            nAlarmId: Int,
            nAlarmType: Int,
            nOrderId: Int,
            nDeliveryState: Int,
            nSelfAssign: Int,
            sServerTime: String,
            sLaunchingTime: String
    ) {
        val sMsg = String.format(Locale.KOREA, "TotalCnt:%d Seq:%d AlarmId:%d AlarmType:%d OrderId:%d DeliveryState:%d SelfAssign:%d"
                ,nTotalCnt, nSeq, nAlarmId, nAlarmType, nOrderId, nDeliveryState, nSelfAssign)
        Log.e("AlarmTest", sMsg)

        // TotalCnt와 Seq와 같을때까지 전달한 값들을 저장해 둔다.
        val alarmInfo = Alarmdata(nTotalCnt, nSeq, nAlarmId, nAlarmType, nOrderId, nDeliveryState, nSelfAssign, sServerTime, sLaunchingTime)
        receivedAlarmInfoList.add(alarmInfo)
        if (nSeq != nTotalCnt) return

        // 마지막것 까지 받았다.
        alarmCallback(receivedAlarmInfoList)
        receivedAlarmInfoList.clear()
    }
}