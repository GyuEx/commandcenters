package com.beyondinc.commandcenter.handler

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Alarmdata
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.data.RiderListdata
import com.beyondinc.commandcenter.net.DACallerInterface
import com.beyondinc.commandcenter.repository.database.entity.*
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
//            try {

                if (Vars.receiveList != null && Vars.receiveList.isNotEmpty()) {

                    var rdata = Vars.receiveList.removeAt(0)

                    val code = rdata.keys.iterator().next()
                    val data = rdata[code]

                    Log.e("Recive", "" + code)
                    Log.e("Recive", "" + data + " // " + data!!.size + " // " )

                    if(code == Procedures.LOGIN)
                    {
                        if(data.size > 0)
                        {
                            //로그인은 반드시 0번지여야함
                            if(data!![0]["CODE"] == "-1000")
                            {
                                Logindata.MSG = data!![0]["MSG"]
                                Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.APK_UPDATE,0).sendToTarget() // 업데이트 시작
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
                                Logindata.SessionExpireMin = (data!![0]["SessionExpireMin"]!!.toInt() * 60)
                                //Logindata.SessionExpireMin = 1 * 60

                                Vars.timecntExit = Logindata.SessionExpireMin

                                if(!data[0].containsKey("PasswdResetYn"))
                                {
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.LOGIN_SUCESS,0).sendToTarget()
                                }
                                else
                                {
                                    if(data[0]["PasswdResetYn"] == "N")
                                    {
                                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.LOGIN_SUCESS,0).sendToTarget()
                                    }
                                    else
                                    {
                                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.CHANGE_PASSWORD,0,data[0]["PasswdResetWhy"].toString()).sendToTarget()
                                    }
                                }
                            }
                            else
                            {
                                if(data!![0]["MSG"] == "비밀번호 변경에 성공하였습니다.")
                                {
                                    isKeep = false
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.DISCONN_ALRAM,0).sendToTarget()

                                    var msg = "비밀번호 변경에 성공하였습니다\n시스템을 다시 시작합니다"
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.SHOW_MESSAGE,0,msg).sendToTarget()
                                }
                                else
                                {
                                    Logindata.MSG = data!![0]["MSG"]
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.LOGIN_FAIL,0).sendToTarget()
                                }
                            }
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
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_CHECK,Finals.INSERT_STORE,0).sendToTarget()
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
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.INSERT_RIDER,0).sendToTarget()
                    }
                    else if(code == Procedures.AGENCY_LIST)
                    {
                        var agencytemp : ConcurrentHashMap<String,Agencydata> = ConcurrentHashMap()
                        for (i in 0 until data!!.size) {
                            val ar = agencypassing(data[i]) // 너무길어서 따로 메소드 처리
                            agencytemp[ar.AgencyId.toString()] = ar
                        }
                        Vars.agencyList.putAll(agencytemp)
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_AGENCY,Finals.INSERT_ORDER,0).sendToTarget()
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_LOADING,0).sendToTarget()
                    }
                    else if(code == Procedures.RIDER_LIST)
                    {
                        var riderlisttemp : ConcurrentHashMap<String,RiderListdata> = ConcurrentHashMap()
                        for (i in 0 until data!!.size) {
                            val ar = riderpassing(data[i]) // 너무길어서 따로 메소드 처리
                            riderlisttemp[ar.RiderId] = ar
                        }
                        Vars.riderlistList.putAll(riderlisttemp)
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_RIDER,Finals.INSERT_ORDER,0).sendToTarget()
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_LOADING,0).sendToTarget()
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
                                    if (now.time > nt.time) {
                                        Vars.centerLastTime[or.RcptCenterId] = or.ModDT
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
                                Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CONN_ALRAM,0).sendToTarget() // 알람켜기
                                Log.e("MainThread" , "일치할걸?")
                            }
                            else
                            {
                                if(allcnt > 5)
                                {
                                    Log.e("MainThread" , "올 클리어 진행")
                                    Vars.orderList.clear() // 마감시간때는 무조건 갯수가 안맞게 되니 하지만 "난" 마감시간을 알수 없음 그러므로 초기화
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.ALL_CLEAR,0).sendToTarget() // 메인아이템뷰 클리어 진행
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.ALL_CLEAR,0).sendToTarget() // 서브아이템뷰 클리어 진행
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CALL_ORDER,0).sendToTarget() // 5번정도 안맞으면 전체리스트 땡겨와
                                    allcnt = 0 // 전체리스트를 두번씩 땡길 필요는 없지
                                }
                                else if(allcnt > 0)
                                {
                                    allcnt++
                                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.DISCONN_ALRAM,0).sendToTarget() // 갯수가 안맞으면 알람을 꺼버림
                                    Vars.timecntOT = 10 // 갯수조회 카운터를 한시적으로 10초로 변경
                                }
                                else
                                {
                                    allcnt++ // 바로 진행하니 부득이한 경우가 생겨서 1회정도는 그냥 체크타임으로 땡기는것이 좋을듯 안맞으면 이 구문 삭제
                                }
                                Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CHECK_TIME,0).sendToTarget() // 데이터가 안맞으면 마지막시간으로 던져서 확인해!
                                Log.e("MainThread" , "일치하지 않음")
                            }
                        }

                        Vars.orderList.putAll(centertemp)

                        if(Vars.mLayer == Finals.SELECT_ORDER && !inToday) Vars.DataHandler!!.obtainMessage(Finals.VIEW_ITEM,Finals.INSERT_ORDER,0).sendToTarget()
                        else if(Vars.mLayer == Finals.SELECT_MAP && !inToday)
                        {
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBITEM,Finals.INSERT_ORDER,0).sendToTarget()
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBRIDER,Finals.MAP_FOR_REFRASH,0).sendToTarget()
                        }
                    }
                    else if(code == Procedures.RIDER_LOCATION_IN_CENTER)
                    {
                        var it = Vars.riderList.keys.iterator()
                        while (it.hasNext())
                        {
                            var itt = it.next()
                            Vars.riderList[itt]?.workingStateCode = Codes.RIDER_OFF_WORK
                        } // 라이더 종료 알람이 잘안들어와서 일단 다 종료로 변경하고 하단에 로케이션 없는것만 불러옴

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
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.ORDER_TOAST_SHOW,0,msg).sendToTarget()
                    }
                    else if(code == Procedures.EDIT_ORDER_INFO)
                    {
                        var msg = ""
                        var msgcode = ""
                        var arrayList : HashMap<String,Any> = HashMap()
                        var sub = 0
                        for(i in 0 until data!!.size)
                        {
                            if(data!![i].size == 4) // 주소검색할때 헤더가 같아서 파라미터 갯수로 구분함 4개는 동검색
                            {
                                sub = 4
                                var dd = Dongdata()
                                dd.code = data!![i]["LawTownCode"].toString()
                                dd.name = data!![i]["LawTownName"].toString()
                                arrayList[dd.name!!] = dd
                            }
                            else if(data!![i].size == 13) // 13개는 주소상세검색
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
                                msgcode = data!![i]["CODE"].toString()
                            }
                        }
                        if(sub == 0 && msgcode != "-6") // result가 동일하므로, 메세지로 구분함 (주소검색이 아닌경우)
                        {
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.ORDER_TOAST_SHOW,0,msg).sendToTarget()
                        }
                        if(sub == 0 && msgcode == "-6") // 주소검색인경우, 서버상태에 따라서 스트링이 변경되면 호출안될수있으니 좋은 방안 필요함 indexOf로 처리할까?
                        {
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ADDRESS,Finals.MESSAGE_ADDR,0).sendToTarget()
                        }
                        else if(sub == 4) // 위와 동일 "동검색"
                        {
                            Vars.dongList.clear() // 한번전체삭제 하고
                            Vars.dongList.putAll(arrayList)
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SHOW_ADDR,0)?.sendToTarget()
                        }
                        else if(sub == 13) // 위와 동일 "주소검색"
                        {
                            Vars.AddrList.clear() // 한번전체삭제 하고
                            Vars.AddrList.putAll(arrayList)
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_ADDRESS,Finals.SEARCH_ADDR,0).sendToTarget()
                        }
                    }
                    else if(code == Procedures.AGENCY_TOWN_LIST)
                    {
                        var arrayList : HashMap<String,Any> = HashMap()
                        for(i in 0 until data!!.size)
                        {
                            var dd = Dongdata()
                            dd.code = data!![i]["LawTownCode"].toString()
                            dd.name = data!![i]["LawTownName"].toString()
                            arrayList[dd.name!!] = dd
                        }
                        Vars.dongList.clear() // 한번전체삭제 하고
                        Vars.dongList.putAll(arrayList)
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SHOW_NEW_ASSIGN,0)?.sendToTarget()
                    }
                    else if(code == Procedures.DELIVERY_FEE_INFO)
                    {
                        var arrayList : HashMap<String,String> = HashMap()
                        for(i in 0 until data!!.size)
                        {
                            arrayList["DeliveryFee"] = data!![i]["DeliveryFee"].toString()
                            arrayList["DeliveryDistance"] = data!![i]["DeliveryDistance"].toString()
                        }
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_ADDRESS,Finals.GET_DELIVERY_FEE,0,arrayList).sendToTarget()
                    }
                    else if(code == Procedures.REG_NEW_ORDER)
                    {
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.ORDER_TOAST_SHOW,0,data!![0]["MSG"]).sendToTarget()
                    }
                }
                Thread.sleep(200)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("MainThread",e.toString())
//            }
        }
    }
    
    fun agencypassing(data : ConcurrentHashMap<String,String>) : Agencydata
    {
        val ar = Agencydata()

        ar.MinDepositAmt = data["MinDepositAmt"].toString()
        ar.BusinessNumber = data["BusinessNumber"].toString()
        ar.AgencyId = data["AgencyId"].toString()
        ar.DetailAddr = data["DetailAddr"].toString()
        ar.Latitude = data["Latitude"].toString()
        ar.WarningDepositAmt = data["WarningDepositAmt"].toString()
        ar.Addr = data["Addr"].toString()
        ar.ArriveAgencyPlanTime = data["ArriveAgencyPlanTime"].toString()
        ar.MobileVanCode = data["MobileVanCode"].toString()
        ar.BusinessCategory = data["BusinessCategory"].toString()
        ar.AgencyOrderDelay = data["AgencyOrderDelay "].toString()
        ar.MinDepositByAgencySettingYn = data["MinDepositByAgencySettingYn"].toString()
        ar.Phone = data["Phone"].toString()
        ar.DailyOrderLimit = data["DailyOrderLimit"].toString()
        ar.CenterAgencyOrderDelay = data["CenterAgencyOrderDelay"].toString()
        ar.MobilePGDeviceCode = data["MobilePGDeviceCode "].toString()
        ar.SumAmt = data["SumAmt"].toString()
        ar.MobileVanDeviceCode = data["MobileVanDeviceCode"].toString()
        ar.DepositYn = data["DepositYn"].toString()
        ar.MonthlyOrderLimitYn = data["MonthlyOrderLimitYn"].toString()
        ar.AgencyName = data["AgencyName"].toString()
        ar.ContactMobile = data["ContactMobile"].toString()
        ar.DeliveryCharge = data["DeliveryCharge"].toString()
        ar.DailyOrderLimitYn = data["DailyOrderLimitYn"].toString()
        ar.MonthlyOrderLimit = data["MonthlyOrderLimit"].toString()
        ar.LoginId = data["LoginId"].toString()
        ar.LatestLoginDT = data["LatestLoginDT"].toString()
        ar.WarningDepositByAgencySettingYn = data["WarningDepositByAgencySettingYn"].toString()
        ar.CEOName = data["CEOName"].toString()
        ar.SurchargeByAgencySettingYn = data["SurchargeByAgencySettingYn"].toString()
        ar.BuildingNo = data["BuildingNo"].toString()
        ar.DeliveryExtFeeType = data["DeliveryExtFeeType"].toString()
        ar.CEOResNo = data["CEOResNo"].toString()
        ar.Maintenance = data["Maintenance"].toString()
        ar.ShowEasyReceiptYn = data["ShowEasyReceiptYn"].toString()
        ar.Longitude = data["Longitude"].toString()
        ar.ShareOrderYn = data["ShareOrderYn"].toString()
        ar.AgencyWorkState = data["AgencyWorkState"].toString()
        ar.CallCenterOrderDelay = data["CallCenterOrderDelay"].toString()
        ar.AgencyLoginYn = data["AgencyLoginYn"].toString()
        ar.State = data["State"].toString()
        ar.DepositWarningPrice = data["DepositWarningPrice"].toString()
        ar.AgencyColor = data["AgencyColor"].toString()
        ar.MobilePGSerialNo = data["MobilePGSerialNo"].toString()
        ar.ActiveYn = data["ActiveYn"].toString()
        ar.MobilePGAgencyId = data["MobilePGAgencyId"].toString()
        ar.StoreTypeId = data["StoreTypeId"].toString()

        return ar
    }
    
    fun riderpassing(data : ConcurrentHashMap<String,String>) : RiderListdata
    {
        val rr = RiderListdata()
        
        rr.InsuranceCompany = data["InsuranceCompany"].toString()
        rr.RiderCallFeeId = data["RiderCallFeeId"].toString()
        rr.LicenseExpireFinish = data["LicenseExpireFinish"].toString()
        rr.DeviceId = data["DeviceId"].toString()
        rr.AttendanceYn = data["AttendanceYn"].toString()
        rr.BalanceLimitAmt = data["BalanceLimitAmt"].toString()
        rr.RiderId = data["RiderId"].toString()
        rr.Latitude = data["Latitude"].toString()
        rr.OffLineColor = data["OffLineColor"].toString()
        rr.RecvAlarmDelayId = data["RecvAlarmDelayId"].toString()
        rr.InsuranceRemark = data["InsuranceRemark"].toString()
        rr.RetiredDt = data["RetiredDt"].toString()
        rr.TaxPaymentTypeId = data["TaxPaymentTypeId"].toString()
        rr.LicenseNo = data["LicenseNo"].toString()
        rr.LicenseExpireFrom = data["LicenseExpireFrom"].toString()
        rr.RetiredRemark = data["RetiredRemark"].toString()
        rr.AgencyDistance = data["AgencyDistance"].toString()
        rr.CurrWorkingOrderCnt = data["CurrWorkingOrderCnt"].toString()
        rr.EatTimeColor = data["EatTimeColor"].toString()
        rr.RiderCallFee = data["RiderCallFee"].toString()
        rr.AssignCancelByRiderYnByRiderSettingYn = data["AssignCancelByRiderYnByRiderSettingYn"].toString()
        rr.AccountBankName = data["AccountBankName"].toString()
        rr.AccountOwner = data["AccountOwner"].toString()
        rr.ShowOrderStatusYn = data["ShowOrderStatusYn"].toString()
        rr.Deposit = data["Deposit"].toString()
        rr.LoginId = data["LoginId"].toString()
        rr.AccountNo = data["AccountNo"].toString()
        rr.AssignCancelByRiderYn = data["AssignCancelByRiderYn"].toString()
        rr.ShareOrderViewTypeId = data["ShareOrderViewTypeId"].toString()
        rr.VehicleType = data["VehicleType"].toString()
        rr.SSNumber = data["SSNumber"].toString()
        rr.Mobile = data["Mobile"].toString()
        rr.InsuranceNo = data["InsuranceNo"].toString()
        rr.Longitude = data["Longitude"].toString()
        rr.JoinDt = data["JoinDt"].toString()
        rr.InsuranceExpireDt = data["InsuranceExpireDt"].toString()
        rr.InputType = data["InputType"].toString()
        rr.JoinRemark = data["JoinRemark"].toString()
        rr.State = data["State"].toString()
        rr.TotalOrderCnt = data["TotalOrderCnt"].toString()
        rr.RiderNewListCntLimit = data["RiderNewListCntLimit"].toString()
        rr.ActiveYn = data["ActiveYn"].toString()
        rr.RiderName = data["RiderName"].toString()
        rr.RiderWorkingCntLimit = data["RiderWorkingCntLimit"].toString()
        
        return rr
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