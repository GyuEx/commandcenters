package com.beyondinc.commandcenter.handler

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import kotlin.concurrent.timer

class CheckThread () : Thread() , ThreadFun {

    private var iskeep = false
    private var time = 0
    private var refrash = 0

    init {
        iskeep = true
    }

    override fun run()
    {
        while (iskeep)
        {
            if(Logindata.CenterList&&Logindata.RiderList&&Logindata.OrderList) //모든정보를 가져왔을때, 쓰레드는 시작된다
            {
                if (time >= Vars.timecntGPS) { // 기본 10초
                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CALL_GPS,0).sendToTarget() // 라이더 위치정보 조회
                    time = 0
                } else {
                    time++
                }
                if (refrash >= Vars.timecntOT) { // 기본 60초
                    if (Vars.mLayer == Finals.SELECT_ORDER) Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget() //시간경과 갱신주기 1분
                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHECK_COUNT,0).sendToTarget() // 카운터를 줌
                    refrash = 0
                } else {
                    refrash++
                }
            }
            Thread.sleep(1000) // 1초
        }
    }

    override fun stopThread() {
        iskeep = false
    }
}