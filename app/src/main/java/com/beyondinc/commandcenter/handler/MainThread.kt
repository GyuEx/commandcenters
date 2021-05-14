package com.beyondinc.commandcenter.handler

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Alarmdata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.net.DACallerInterface
import com.beyondinc.commandcenter.repository.database.entity.Centerdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import com.vasone.deliveryalarm.DAClient
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
                        if(data!![0]["CODE"] == "-1000")
                        {

                        }
                        else if(data!![0]["MSG"] == "로그인 성공!")
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
                        //Log.e("여기가","호출이 되는지 알구싶다!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                        var ridertemp : ConcurrentHashMap<String,Riderdata> = ConcurrentHashMap()
                        for (i in 0 until data!!.size) {
                            val ri = Riderdata()
                            ri.id = data[i]["RiderId"].toString()
                            ri.centerID = data[i]["CenterId"].toString()
                            ri.name = data[i]["RiderName"].toString()
                            ri.isEatTime = data[i]["EatTimeYn"].toString()
                            ri.workingStateCode = data[i]["RunningState"].toString()
                            ridertemp[ri.id!!] = ri
                        }

                        Vars.riderList.putAll(ridertemp)
                        Vars.MainsHandler!!.obtainMessage(Finals.INSERT_RIDER).sendToTarget()
                    }
                    else if(code == Procedures.ORDER_LIST_IN_CENTER)
                    {
                        var centertemp : ConcurrentHashMap<String,Orderdata> = ConcurrentHashMap()
                        for (i in 0 until data!!.size) {
                            if(data[i].containsKey("SumOfToday"))
                            {
                                if(Vars.centerOrderCount[data[i]["CenterId"]!!] != data[i]["SumOfToday"])
                                {
                                    Log.e("MainThread" , "일치하지 않음 // ${Vars.centerOrderCount[data[i]["CenterId"]!!]} // ${data[i]["SumOfToday"]}")
                                }
                            }
                            else
                            {
                                val or = passing(data[i]) // 너무길어서 따로 메소드 처리
                                centertemp[or.OrderId] = or
                                Vars.centerLastTime[or.CenterId] = or.ModDT
                            }
                        }
                        Vars.orderList.putAll(centertemp)
                        if(Vars.mLayer == Finals.SELECT_ORDER) Vars.ItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
                        else if(Vars.mLayer == Finals.SELECT_MAP)
                        {
                            Vars.SubItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
                            Vars.SubRiderHandler!!.obtainMessage(Finals.MAP_FOR_REFRASH).sendToTarget()
                        }
                    }
                    else if(code == Procedures.RIDER_LOCATION_IN_CENTER)
                    {
                        for (i in 0 until data!!.size) {

                            var id = data[i]["DriverId"].toString()
                            var rx = data[i]["LastLocationLongitude"].toString()
                            var ry = data[i]["LastLocationLatitude"].toString()
                            var rt = data[i]["LastLocationModDT"].toString()

                            Vars.riderList[id]?.latitude = ry
                            Vars.riderList[id]?.longitude = rx
                            Vars.riderList[id]?.ModDT = rt
                            Vars.riderList[id]?.workingStateCode = Codes.RIDER_ON_WORK
                        }
                        MarkerThread().start()
                    }
                    else if(code == Procedures.CHANGE_DELIVERY_STATUS)
                    {
                        var msg : String = ""
                        for(i in 0 until data!!.size) {
                            msg = data!![i]["MSG"].toString()
                        }
                        Vars.MainsHandler!!.obtainMessage(Finals.ORDER_TOAST_SHOW,msg).sendToTarget()
                    }
                    else if(code == Procedures.EDIT_ORDER_INFO)
                    {
                        var msg : String = ""
                        for(i in 0 until data!!.size) {
                            msg = data!![i]["MSG"].toString()
                        }
                        Vars.MainsHandler!!.obtainMessage(Finals.ORDER_TOAST_SHOW,msg).sendToTarget()
                    }
                }
                Thread.sleep(200)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("MainThread",e.toString())
//            }
        }
    }

    fun passing(data : ConcurrentHashMap<String, String>) : Orderdata
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