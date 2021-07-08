package com.beyondinc.commandcenter.handler

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.*
import org.json.simple.JSONArray
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlarmThread() : Thread() , ThreadFun{

    var isKeep : Boolean = false

    init {
        isKeep = true
    }

    override fun stopThread() {
        isKeep = false
    }

    override fun run() {
        while (isKeep) {
            try {

                if (Vars.alarmList != null && Vars.alarmList.isNotEmpty()) {

                    var rdata = Vars.alarmList.removeAt(0)

                    //Log.e("Alram" , "" + rdata.mnAlarmType + " // " + rdata.mnOrderId)

                    if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_ORDER)
                    {
                        var list : ArrayList<String> = ArrayList()
                        list.add(rdata.mnOrderId.toString())
                        var temp : ConcurrentHashMap<String, JSONArray> = ConcurrentHashMap()
                        temp[Procedures.ORDER_DETAIL] = MakeJsonParam().makeOrderDetailParameter(Logindata.LoginId!!,list)
                        Vars.sendList.add(temp)

                        if(Logindata.OrderList && Logindata.RiderList && Logindata.CenterList){
                            PlaySoundThread(rdata.mnOrderId,rdata.mnDeliveryState).start() // 소리재생
                        }
                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_DEPOSIT)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_NOTICE)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_ORDER_WORKENV)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_MESSAGE)
                    {

                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_RIDER_WORK_STATE)
                    {
                        if(Vars.riderList.containsKey(rdata.mnOrderId.toString()))
                        {
                            when (rdata.mnDeliveryState)
                            {
                                AlarmCodes.RIDER_WORK_STATE_ON_WORK -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_ON_WORK
                                AlarmCodes.RIDER_WORK_STATE_OFF_WORK -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_OFF_WORK
                                AlarmCodes.RIDER_WORK_STATE_START_EAT_TIME -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_ON_EAT
                                AlarmCodes.RIDER_WORK_STATE_END_EAT_TIME -> Vars.riderList[rdata.mnOrderId.toString()]!!.workingStateCode = Codes.RIDER_ON_WORK
                            }
                        }
                    }
                    else if(rdata.mnAlarmType == AlarmCodes.ALARM_TYPE_CHATTING)
                    {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("AlramThread",e.toString())
            }
            Thread.sleep(200)
        }
    }
}