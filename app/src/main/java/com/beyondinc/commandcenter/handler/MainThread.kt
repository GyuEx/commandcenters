package com.beyondinc.commandcenter.handler

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Alarmdata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.net.DACallerInterface
import com.beyondinc.commandcenter.repository.database.entity.Addrdata
import com.beyondinc.commandcenter.repository.database.entity.Centerdata
import com.beyondinc.commandcenter.repository.database.entity.Dongdata
import com.beyondinc.commandcenter.repository.database.entity.Riderdata
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import com.vasone.deliveryalarm.DAClient
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainThread() : Thread() , ThreadFun{

    private var isKeep : Boolean = false
    private var allcnt = 0

    init {
        isKeep = true
    }

    override fun stopThread() {
        isKeep = false
    }

    override fun run() {
        while (isKeep) {
            try {

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
                            Logindata.MSG = data!![0]["MSG"]
                            Vars.LoginHandler!!.obtainMessage(Finals.APK_UPDATE).sendToTarget()
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
                        var inToday = false
                        var orcnt = 0
                        var centertemp : ConcurrentHashMap<String,Orderdata> = ConcurrentHashMap()
                        for (i in 0 until data!!.size)
                        {
                            if(data[i].containsKey("SumOfToday"))
                            {
                                inToday = true
                                if(Vars.centerOrderCount[data[i]["CenterId"]!!] != data[i]["SumOfToday"])
                                {
                                    orcnt++
                                }
                            }
                            else
                            {
                                val or = passing(data[i]) // 너무길어서 따로 메소드 처리

                                //오더시간을 계산해보자
                                if(Vars.centerLastTime.containsKey(or.RcptCenterId)) {
                                    var ft = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    var now = ft.parse(or.ModDT)
                                    var nt = ft.parse(Vars.centerLastTime[or.RcptCenterId])
                                    Log.e(
                                        "Time",
                                        "${or.ModDT} 이거랑 ${Vars.centerLastTime[or.RcptCenterId]}"
                                    )
                                    if (now.time > nt.time) {
                                        Vars.centerLastTime[or.RcptCenterId] = or.ModDT
                                        Log.e("Time", "들어왔다!")
                                    }
                                }
                                else
                                {
                                    Vars.centerLastTime[or.RcptCenterId] = or.ModDT
                                }
                                // 시간정보는 데이터 수집과 동시에 진행한다, 물론 기존시간이랑 비교해서 큰지 안큰지 확인은 필수!
                                centertemp[or.OrderId] = or
                            }
                        }

                        if(inToday)
                        {
                            if(orcnt == 0)
                            {
                                Vars.timecntOT = 60 // 체크 주기 다시 1분으로 변경하고
                                allcnt = 0 // 재시도 횟수 0으로 초기화
                                Vars.MainsHandler!!.obtainMessage(Finals.CONN_ALRAM).sendToTarget() // 알람켜기
                                Log.e("MainThread" , "일치할걸?")
                            }
                            else
                            {
                                if(allcnt > 5)
                                {
                                    Log.e("MainThread" , "올 클리어 진행")
                                    Vars.orderList.clear() // 마감시간때는 무조건 갯수가 안맞게 되니 하지만 "난" 마감시간을 알수 없음 그러므로 초기화
                                    Vars.ItemHandler!!.obtainMessage(Finals.ALL_CLEAR).sendToTarget() // 메인아이템뷰 클리어 진행
                                    Vars.SubItemHandler!!.obtainMessage(Finals.ALL_CLEAR).sendToTarget() // 서브아이템뷰 클리어 진행
                                    Vars.MainsHandler!!.obtainMessage(Finals.CALL_ORDER).sendToTarget() // 5번정도 안맞으면 전체리스트 땡겨와
                                    allcnt = 0 // 전체리스트를 두번씩 땡길 필요는 없지
                                }
                                else if(allcnt > 0)
                                {
                                    allcnt++
                                    Vars.MainsHandler!!.obtainMessage(Finals.DISCONN_ALRAM).sendToTarget() // 갯수가 안맞으면 알람을 꺼버림
                                    Vars.timecntOT = 10 // 갯수조회 카운터를 한시적으로 10초로 변경
                                }
                                else
                                {
                                    allcnt++ // 바로 진행하니 부득이한 경우가 생겨서 1회정도는 그냥 체크타임으로 땡기는것이 좋을듯 안맞으면 이 구문 삭제
                                }
                                Vars.MainsHandler!!.obtainMessage(Finals.CHECK_TIME).sendToTarget() // 데이터가 안맞으면 마지막시간으로 던져서 확인해!
                                Log.e("MainThread" , "일치하지 않음")
                            }
                        }

                        Vars.orderList.putAll(centertemp)

                        if(Vars.mLayer == Finals.SELECT_ORDER && !inToday) Vars.ItemHandler!!.obtainMessage(Finals.INSERT_ORDER).sendToTarget()
                        else if(Vars.mLayer == Finals.SELECT_MAP && !inToday)
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
                        var msg = ""
                        for(i in 0 until data!!.size) {
                            msg = data!![i]["MSG"].toString()
                        }
                        Vars.MainsHandler!!.obtainMessage(Finals.ORDER_TOAST_SHOW,msg).sendToTarget()
                    }
                    else if(code == Procedures.EDIT_ORDER_INFO)
                    {
                        var msg = ""
                        var arrayList : HashMap<String,Any> = HashMap()
                        var sub = 0
                        for(i in 0 until data!!.size)
                        {
                            if(data!![i].size == 4)
                            {
                                sub = 4
                                var dd = Dongdata()
                                dd.code = data!![i]["LawTownCode"].toString()
                                dd.name = data!![i]["LawTownName"].toString()
                                arrayList[dd.name!!] = dd
                            }
                            else if(data!![i].size == 13)
                            {
                                sub = 13
                                var ad = Addrdata()
                                ad.BuildingManageNo = data!![i]["BuildingManageNo"].toString()
                                ad.CityName = data!![i]["CityName"].toString()
                                ad.CountyName = data!![i]["CountyName"].toString()
                                ad.Jibun = data!![i]["Jibun"].toString()
                                ad.JibunAddr = data!![i]["JibunAddr"].toString()
                                ad.Latitude = data!![i]["Latitude"].toString()
                                ad.Longitude = data!![i]["Longitude"].toString()
                                ad.Road = data!![i]["Road"].toString()
                                ad.RoadAddr = data!![i]["RoadAddr"].toString()
                                ad.VillageName = data!![i]["VillageName"].toString()
                                ad.LawTownName = data!![i]["LawTownName"].toString()
                                arrayList[ad.BuildingManageNo!!] = ad
                            }
                            else
                            {
                                msg = data!![i]["MSG"].toString()
                            }
                        }
                        if(sub == 0 && msg != "조회결과가 존재하지 않습니다.")
                        {
                            Vars.MainsHandler!!.obtainMessage(Finals.ORDER_TOAST_SHOW,msg).sendToTarget()
                        }
                        if(sub == 0 && msg == "조회결과가 존재하지 않습니다.")
                        {
                            if(Vars.AddressHandler != null) Vars.AddressHandler!!.obtainMessage(Finals.MESSAGE_ADDR).sendToTarget()
                        }
                        else if(sub == 4)
                        {
                            Vars.dongList.clear() // 한번전체삭제 하고
                            Vars.dongList.putAll(arrayList)
                            Vars.MainsHandler?.obtainMessage(Finals.SHOW_ADDR)?.sendToTarget()
                        }
                        else if(sub == 13)
                        {
                            Vars.AddrList.clear() // 한번전체삭제 하고
                            Vars.AddrList.putAll(arrayList)
                            Vars.AddressHandler?.obtainMessage(Finals.SEARCH_ADDR)?.sendToTarget() //Addr은 Null이 널일수 있음!
                        }
                    }
                }
                Thread.sleep(200)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainThread",e.toString())
            }
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