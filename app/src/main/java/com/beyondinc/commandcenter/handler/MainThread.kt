package com.beyondinc.commandcenter.handler

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.data.Checkdata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.repository.database.entity.Centerdata
import com.beyondinc.commandcenter.repository.database.entity.Order
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class MainThread() : Thread() , ThreadFun{

    var isKeep : Boolean = false

    init {
        isKeep = true
    }

    override fun stopThread() {
        isKeep = false
    }

    override fun run() {
        while (isKeep) {
//            try {

                if (Vars.receiveList != null && Vars.receiveList.isNotEmpty()) {

                    var rdata = Vars.receiveList.removeAt(0)

                    val code = rdata.keys.iterator().next()
                    val data = rdata[code]

                    Log.e("Recive", "" + code)
                    Log.e("Recive", "" + data + " // " + data!!.size + " // " )

                    if(code == Procedures.LOGIN)
                    {
                        //로그인은 반드시 0번지여야함
                        if(data!![0]["MSG"] == "로그인 성공!")
                        {
                            Logindata.CenterId=data!![0]["CenterId"]
                            Logindata.CenterName=data!![0]["CenterName"]
                            Logindata.CompanyId=data!![0]["CompanyId"]
                            Logindata.CompanyName=data!![0]["CompanyName"]
                            Logindata.UserDesc=data!![0]["UserDesc"]
                            Logindata.LastLoginDT=data!![0]["LastLoginDT"]
                            Logindata.MSG=data!![0]["MSG"]
                            Vars.LoginHandler!!.obtainMessage(Finals.LOGIN_SUCESS).sendToTarget()
                        }
                        else
                        {
                            Logindata.MSG = data!![0]["MSG"]
                            Vars.LoginHandler!!.obtainMessage(Finals.LOGIN_FAIL).sendToTarget()
                        }
                    }
                    else if(code == Procedures.CENTER_LIST)
                    {
                        for(i in 0 until data!!.size)
                        {
                            if(data!![i]["MSG"] == "성공")
                            {
                                val ct = Centerdata()
                                ct.companyName = data[i]["CompanyName"].toString()
                                ct.centerName = data[i]["CenterName"].toString()
                                ct.companyId = data[i]["CompanyId"].toString()
                                ct.centerId = data[i]["CenterId"].toString()
                                Vars.centerList[ct.centerId!!] = ct
                            }
                        }
                        Vars.CheckHandler!!.obtainMessage(Finals.INSERT_STORE).sendToTarget()
                    }
                    else if(code == Procedures.RIDER_LIST_IN_CENTER)
                    {
                        var ridertemp : HashMap<String,Riderdata> = HashMap()
                        var centertemp : HashMap<String,String> = HashMap()
                        for (i in 0 until data!!.size) {
                            val ri = Riderdata()
                            ri.id = data[i]["RiderId"].toString()
                            ri.centerID = data[i]["CenterId"].toString()
                            ri.name = data[i]["RiderName"].toString()
                            ri.isEatTime = data[i]["EatTimeYn"].toString()
                            ri.workingStateCode = data[i]["RunningState"].toString()
                            ridertemp[ri.id!!] = ri
                            centertemp[ri.centerID!!] = ri.centerID!!
                        }

                        var it : Iterator<String> = centertemp.keys.iterator()
                        while (it.hasNext())
                        {
                            var ctemp = centertemp[it.next()]
                            var itemp : ConcurrentHashMap<String,Riderdata> = ConcurrentHashMap()
                            var rit : Iterator<String> = ridertemp.keys.iterator()
                            while (rit.hasNext())
                            {
                                var rtemp = ridertemp[rit.next()]
                                if(rtemp!!.centerID == ctemp)
                                {
                                    itemp[rtemp.id!!] = rtemp
                                }
                            }
                            Vars.riderList[ctemp.toString()] = itemp
                        }
                        Vars.MainsHandler!!.obtainMessage(Finals.INSERT_RIDER).sendToTarget()
                    }
                    else if(code == Procedures.ORDER_LIST_IN_CENTER)
                    {
                        var ridertemp : HashMap<String,Orderdata> = HashMap()
                        var centertemp : HashMap<String,String> = HashMap()
                        for (i in 0 until data!!.size) {
                            val or = passing(data[i]) // 너무길어서 따로 메소드 처리
                            ridertemp[or.OrderId] = or
                            centertemp[or.CenterId] = or.CenterId
                        }

                        var it : Iterator<String> = centertemp.keys.iterator()
                        while (it.hasNext())
                        {
                            var ctemp = centertemp[it.next()]
                            var itemp : ConcurrentHashMap<String,Orderdata> = ConcurrentHashMap()
                            var rit : Iterator<String> = ridertemp.keys.iterator()
                            while (rit.hasNext())
                            {
                                var rtemp = ridertemp[rit.next()]
                                if(rtemp!!.CenterId == ctemp)
                                {
                                    itemp[rtemp.OrderId!!] = rtemp
                                }
                            }
                            Vars.orderList[ctemp.toString()] = itemp
                        }
                        Vars.ItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
                        Vars.SubItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
                    }
                    else if(code == Procedures.RIDER_LOCATION_IN_CENTER)
                    {
                        for (i in 0 until data!!.size) {

                            var id = data[i]["DriverId"].toString()
                            var rx = data[i]["LastLocationLongitude"].toString()
                            var ry = data[i]["LastLocationLatitude"].toString()
                            var rt = data[i]["LastLocationModDT"].toString()

                            var it : Iterator<String> = Vars.centerList.keys.iterator()
                            while (it.hasNext())
                            {
                                var ct = it.next()
                                if(Vars.riderList[ct]?.containsKey(id) == true)
                                {
                                    Vars.riderList[ct]?.get(id)?.latitude = ry
                                    Vars.riderList[ct]?.get(id)?.longitude = rx
                                    Vars.riderList[ct]?.get(id)?.ModDT = rt
                                    Vars.riderList[ct]?.get(id)?.workingStateCode = Codes.RIDER_ON_WORK
                                }
                            }

                            //라이더가 운행중인 상태인지 아닌지 여부 확인(출퇴근 처리가 좀 애매한것 같음)
                            var rit : Iterator<String> = Vars.riderList.keys.iterator()
                            while (rit.hasNext())
                            {
                                var rits = rit.next()
                                var ritsr : Iterator<String> = Vars.riderList[rits]!!.keys.iterator()
                                while (ritsr.hasNext())
                                {
                                    var ritsrr = ritsr.next()
                                    val dateform = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko","KR"))
                                    var longTime = dateform.parse(Vars.riderList[rits]?.get(ritsrr)?.ModDT).time
                                    var sysTime : Long = System.currentTimeMillis()
                                    if((sysTime - longTime) > 60) Vars.riderList[rits]?.get(ritsrr)?.workingStateCode = Codes.RIDER_OFF_WORK
                                }
                            }
                        }
                        Vars.MapHandler!!.obtainMessage(Finals.CREATE_RIDER_MARKER).sendToTarget()
                        Vars.SubRiderHandler!!.obtainMessage(Finals.INSERT_RIDER).sendToTarget()
                    }
                }
                Thread.sleep(200)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("MainThread",e.toString())
//            }
        }
    }

    fun passing(data : HashMap<String, String>) : Orderdata
    {
        val or = Orderdata()

        or.RcptCenterId = data["RcptCenterId"].toString()
        or.ModDT = data["ModDT"].toString()
        or.EnvColorAssign = data["EnvColorAssign"].toString()
        or.ReceiptSource = data["ReceiptSource"].toString()
        or.ReceiptDT = data["ReceiptDT"].toString()
        or.AgencyGroupColor = data["AgencyGroupColor"].toString()
        or.RiderId = data["RiderId"].toString()
        or.KWeight = data["KWeight"].toString()
        or.CustomerShortAddrNoRoad = data["CustomerShortAddrNoRoad"].toString()
        or.AgencyArrivePlanDT = data["AgencyArrivePlanDT"].toString()
        or.CustomerPhone = data["CustomerPhone"].toString()
        or.DoingOtherCenter = data["DoingOtherCenter"].toString()
        or.ChangePayTypeColor = data["ChangePayTypeColor"].toString()
        or.ApprovalType = data["ApprovalType"].toString()
        or.EnvColorPickup = data["EnvColorPickup"].toString()
        or.AgencyLatitude = data["AgencyLatitude"].toString()
        or.DeviceUniqId = data["DeviceUniqId"].toString()
        or.PackingCompleteYn = data["PackingCompleteYn"].toString()
        or.StoreTypeName = data["StoreTypeName"].toString()
        or.ReceiptDTDisplay = data["ReceiptDTDisplay"].toString()
        or.RiderWorkingCnt = data["RiderWorkingCnt"].toString()
        or.AgencyAddr = data["AgencyAddr"].toString()
        or.ChangePayTypeName = data["ChangePayTypeName"].toString()
        or.DeliveryFeeEtc = data["DeliveryFeeEtc"].toString()
        or.WorkingApprovalType = data["WorkingApprovalType"].toString()
        or.CompleteDT = data["CompleteDT"].toString()
        or.CancelDT = data["CancelDT"].toString()
        or.CustomerDetailAddr = data["CustomerDetailAddr"].toString()
        or.OrderId = data["OrderId"].toString()
        or.ServerTime = data["ServerTime"].toString()
        or.CustomerAddrData = data["CustomerAddrData"].toString()
        or.CenterName = data["CenterName"].toString()
        or.OrderClass = data["OrderClass"].toString()
        or.CustomerShortAddr = data["CustomerShortAddr"].toString()
        or.DeliveryFee = data["DeliveryFee"].toString()
        or.OrderBackColor = data["OrderBackColor"].toString()
        or.AssignCenterId = data["AssignCenterId"].toString()
        or.EndApprovalType = data["EndApprovalType"].toString()
        or.EnvColorCancel = data["EnvColorCancel"].toString()
        or.ShareRegUserId = data["ShareRegUserId"].toString()
        or.AgencyId = data["AgencyId"].toString()
        or.ShareOrderColor = data["ShareOrderColor"].toString()
        or.AgencyLongitude = data["AgencyLongitude"].toString()
        or.AgencyAddrData = data["AgencyAddrData"].toString()
        or.CustomerArriveDT = data["CustomerArriveDT"].toString()
        or.AgencyPhoneNo = data["AgencyPhoneNo"].toString()
        or.ShareOrderYN = data["ShareOrderYN"].toString()
        or.DriverAssignUserId = data["DriverAssignUserId"].toString()
        or.ChangePayTypeShortName = data["ChangePayTypeShortName"].toString()
        or.DeliveryStateName = data["DeliveryStateName"].toString()
        or.CustomerLongAddr = data["CustomerLongAddr"].toString()
        or.SalesPrice = data["SalesPrice"].toString()
        or.StoreMemo = data["StoreMemo"].toString()
        or.EnvColorChangePayType = data["EnvColorChangePayType"].toString()
        or.AfterAssignOrderDelay = data["AfterAssignOrderDelay"].toString()
        or.AssignCancelOrderYn = data["AssignCancelOrderYn"].toString()
        or.EnvColorAssignCancel = data["EnvColorAssignCancel"].toString()
        or.AssignCancelOrderColor = data["AssignCancelOrderColor"].toString()
        or.CustomerLatitude = data["CustomerLatitude"].toString()
        or.AgencyRequestTime = data["AgencyRequestTime"].toString()
        or.AgencyName = data["AgencyName"].toString()
        or.CustomerLongitude = data["CustomerLongitude"].toString()
        or.OrderConfirmDT = data["OrderConfirmDT"].toString()
        or.LoginId = data["LoginId"].toString()
        or.EnvColorReceipt = data["EnvColorReceipt"].toString()
        or.StartApprovalType = data["StartApprovalType"].toString()
        or.DriverAssignDT = data["DriverAssignDT"].toString()
        or.RiderPhoneNo = data["RiderPhoneNo"].toString()
        or.ReceiptId = data["ReceiptId"].toString()
        or.DeliveryState = data["DeliveryState"].toString()
        or.DriverConfirmDT = data["DriverConfirmDT"].toString()
        or.PickupDT = data["PickupDT"].toString()
        or.CenterId = data["CenterId"].toString()
        or.ApprovalTypeName = data["ApprovalTypeName"].toString()
        or.DeliveryDistance = data["DeliveryDistance"].toString()
        or.OuterOrderNo = data["OuterOrderNo"].toString()
        or.EnvColorComplete = data["EnvColorComplete"].toString()
        or.RiderName = data["RiderName"].toString()
        or.StoreTypeId = data[".StoreTypeId"].toString()

        return or
    }
}