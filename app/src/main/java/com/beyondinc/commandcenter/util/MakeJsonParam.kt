package com.beyondinc.commandcenter.util

import com.beyondinc.commandcenter.data.Logindata
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

class MakeJsonParam {

    fun makeLoginParameter(id: String, password: String): JSONArray {
        val md5EncryptedPassword = Crypto.generateMD5Hash(password)!!
        val encryptedPassword = Crypto.generateSHA512Hash(
                md5EncryptedPassword.toLowerCase(Locale.ROOT))!!.toUpperCase(Locale.ROOT)

        val parameterArray = JSONArray()

        val parameterJSON = JSONObject()
        parameterJSON["AppType"] = Logindata.AppType
        parameterJSON["DeviceId"] = Logindata.deviceID
        parameterJSON["LoginId"] = id
        parameterJSON["Passwd"] = encryptedPassword
        parameterJSON["AppId"] = Logindata.appID
        parameterJSON["ReqType"] = Procedures.UserAuthType.LOGIN
        parameterJSON["AppVersion"] = Logindata.appVersion
        parameterJSON["ModelName"] = Logindata.deviceModel
        parameterJSON["PhoneNo"] = Logindata.devicePhone
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeChangePasswordParameter(id: String, oldpassword: String, newpassword : String): JSONArray {
        val md5EncryptedPassword = Crypto.generateMD5Hash(oldpassword)!!
        val encryptedPassword = Crypto.generateSHA512Hash(md5EncryptedPassword.toLowerCase(Locale.ROOT))!!.toUpperCase(Locale.ROOT)

        val Newmd5EncryptedPassword = Crypto.generateMD5Hash(newpassword)!!
        val NewencryptedPassword = Crypto.generateSHA512Hash(Newmd5EncryptedPassword.toLowerCase(Locale.ROOT))!!.toUpperCase(Locale.ROOT)

        val parameterArray = JSONArray()

        val parameterJSON = JSONObject()
        parameterJSON["AppType"] = Logindata.AppType
        parameterJSON["DeviceId"] = Logindata.deviceID
        parameterJSON["LoginId"] = id
        parameterJSON["Passwd"] = encryptedPassword
        parameterJSON["AppId"] = Logindata.appID
        parameterJSON["ReqType"] = Procedures.UserAuthType.CHANGE_PASSWORD
        parameterJSON["AppVersion"] = Logindata.appVersion
        parameterJSON["ModelName"] = Logindata.deviceModel
        parameterJSON["PhoneNo"] = Logindata.devicePhone
        parameterJSON["NewPasswd"] = NewencryptedPassword
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    private fun makeBaseParameter(loginID: String): JSONObject {
        val parameterJSON = JSONObject()

        parameterJSON["AppType"] = Logindata.AppType
        parameterJSON["DeviceId"] = Logindata.deviceID
        parameterJSON["LoginId"] = loginID
        parameterJSON["AppId"] = Logindata.appID
        parameterJSON["PhoneNo"] = Logindata.devicePhone

        return parameterJSON
    }

    fun makeCenterListParameter(loginID: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeLogoutParameter(loginID: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = Procedures.UserAuthType.LOGOUT
        parameterJSON["ModelName"] = Logindata.deviceModelNumber
        parameterArray.add(parameterJSON)

        return parameterArray
    }

//    fun makeChangePasswordParameter(loginID: String,
//                                    oldPassword: String, newPassword: String): JSONArray {
//        val parameterArray = JSONArray()
//
//        val parameterJSON = makeBaseParameter(loginID)
//        parameterJSON["ReqType"] = "Change.Passwd"
//        parameterJSON["Passwd"] = oldPassword
//        parameterJSON["NewPasswd"] = newPassword
//        parameterArray.add(parameterJSON)
//
//        return parameterArray
//    }

    fun makeRiderListParameter(loginID: String, centerIDs: List<String>): JSONArray {
        val parameterArray = JSONArray()

        for (centerID in centerIDs) {
            val parameterJSON = makeBaseParameter(loginID)
            parameterJSON["CenterId"] = centerID
            parameterJSON["ReqType"] = "Rider.List"
            parameterArray.add(parameterJSON)
        }

        return parameterArray
    }

    fun makeFullOrderListParameter(loginID: String, centerIDSet: List<String>): JSONArray {
        val parameterArray = JSONArray()

        for (centerID in centerIDSet) {
            val parameterJSON = makeBaseParameter(loginID)
            parameterJSON["CenterId"] = centerID
            parameterJSON["InputData"] = ""
            parameterJSON["ReqType"] = "0"
            parameterArray.add(parameterJSON)
        }

        return parameterArray
    }

    fun makeChangedOrderListParameter(loginID: String, centerInfoSet: ConcurrentHashMap<String,String>): JSONArray
    {
        val parameterArray = JSONArray()
        for (centerInfo in centerInfoSet.keys) {
            val parameterJSON = makeBaseParameter(loginID)
            parameterJSON["CenterId"] = centerInfo
            parameterJSON["InputData"] = centerInfoSet[centerInfo]
            parameterJSON["ReqType"] = "2"
            parameterArray.add(parameterJSON)
        }

        return parameterArray
    }

    fun makeServerOrderListCountParameter(loginID: String, centerInfoSet: ConcurrentHashMap<String,String>): JSONArray
    {
        val parameterArray = JSONArray()
        for (centerInfo in centerInfoSet.keys) {
            val parameterCheckSize = makeBaseParameter(loginID)
            parameterCheckSize["CenterId"] = centerInfo
            parameterCheckSize["InputData"] = centerInfoSet[centerInfo]
            parameterCheckSize["ReqType"] = "3"
            parameterArray.add(parameterCheckSize)
        }
        return parameterArray
    }

    fun makeOrderDetailParameter(loginID: String, orderIDs: List<String>): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = "1"
        val orderIDSet = StringBuilder()
        for (index in orderIDs.indices) {
            if (orderIDSet.isNotEmpty() || index > 0) {
                orderIDSet.append(",")
            }
            orderIDSet.append(orderIDs[index])
        }
        parameterJSON["InputData"] = orderIDSet.toString()
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeAssignOrderParameter(loginID: String, orderID: String, assignType: String, riderID: String): JSONArray {
        val parameterArray = JSONArray()
            val parameterJSON = makeBaseParameter(loginID)
            parameterJSON["OrderId"] = orderID
            parameterJSON["RiderId"] = riderID
            parameterJSON["ReqType"] = assignType
            parameterArray.add(parameterJSON)
        return parameterArray
    }

    fun makeAssignOrderListParameter(loginID: String, assignSet: ArrayList<String>, assignType: String, riderID: String): JSONArray {
        val parameterArray = JSONArray()

        for (assignInfo in assignSet) {
            val parameterJSON = makeBaseParameter(loginID)
            parameterJSON["OrderId"] = assignInfo
            parameterJSON["RiderId"] = riderID
            parameterJSON["ReqType"] = assignType
            parameterArray.add(parameterJSON)
        }

        return parameterArray
    }


    fun makeChangeOrderStatusParameter(loginID: String, orderID: String, newStatusCode: String,
                                       riderID: String, endApproval: String?): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["OrderId"] = orderID
        parameterJSON["ReqType"] = newStatusCode
        if (newStatusCode == Procedures.ChangeStatusType.ORDER_FORCE_COMPLETE
                && riderID.isNotEmpty() && endApproval != null) {
            parameterJSON["RiderId"] = riderID
            parameterJSON["EndApproval"] = endApproval
        }
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    private fun makeEditInfoParameter(loginID: String, editInfoType: String, editOrderID: String): JSONObject {
        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = editInfoType
        parameterJSON["OrderId"] = editOrderID

        return parameterJSON
    }

    fun makeAgencyTownListParameter(loginID: String, editOrderID: String,
                                    centerID: String, agencyID: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeEditInfoParameter(loginID, Procedures.EditInfoType.AGENCY_TOWN_LIST, editOrderID)
        parameterJSON["CenterId"] = centerID
        parameterJSON["AgencyId"] = agencyID
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeAddressListParameter(loginID: String, editOrderID: String,
                                 centerID: String, agencyID: String, wordType: String,
                                 lawTownCode: String, keyword: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON =
                makeEditInfoParameter(loginID, Procedures.EditInfoType.SEARCH_ADDRESS, editOrderID)
        parameterJSON["CenterId"] = centerID
        parameterJSON["AgencyId"] = agencyID
        parameterJSON["SearchAddrWordType"] = wordType
        parameterJSON["SearchWord"] = keyword
        parameterJSON["LawTownCode"] = lawTownCode
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeChangeDeliveryAddressParameter(loginID: String, editOrderID: String,
                                           regionAddress: String, detailAddress: String,
                                           lotCode: String, shortRoadAddress: String,
                                           lawTownName: String, buildingManageNumber: String,
                                           latitude: String, longitude: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON =
                makeEditInfoParameter(loginID, Procedures.EditInfoType.CHANGE_DELIVERY_ADDRESS, editOrderID)
        parameterJSON["Addr"] = regionAddress
        parameterJSON["LawTownName"] = lawTownName
        parameterJSON["Jibun"] = lotCode
        parameterJSON["DetailAddr"] = detailAddress
        parameterJSON["BuildingManageNo"] = buildingManageNumber
        parameterJSON["GPSLatitude"] = latitude
        parameterJSON["GPSLongitude"] = longitude
        parameterJSON["ShortRoadAddr"] = shortRoadAddress
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeOnPickupReadyParameter(loginID: String, editOrderID: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeEditInfoParameter(loginID, Procedures.EditInfoType.ORDER_PACKING_COMPLETE, editOrderID)
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeChangeSalesPriceParameter(loginID: String, editOrderID: String, newPrice: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeEditInfoParameter(loginID, Procedures.EditInfoType.CHANGE_SALES_PRICE, editOrderID)
        parameterJSON["SalesPrice"] = newPrice
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeChangeApprovalTypeParameter(loginID: String, editOrderID: String, Approval: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeEditInfoParameter(loginID, Procedures.EditInfoType.APPROVAL_TYPE, editOrderID)
        parameterJSON["ApprovalType"] = Approval
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeAddDeliveryFeeParameter(loginID: String, editOrderID: String, extraFee: String): JSONArray {
        val parameterArray = JSONArray()

        val parameterJSON = makeEditInfoParameter(loginID, Procedures.EditInfoType.ADD_DELIVERY_FEE, editOrderID)
        parameterJSON["AddDelivertyFee"] = extraFee
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeRidersLocationParameter(loginID: String, centerIDs: List<String>): JSONArray {
        val parameterArray = JSONArray()

        for (centerID in centerIDs) {
            val parameterJSON = makeBaseParameter(loginID)
            parameterJSON["CenterId"] = centerID
            parameterArray.add(parameterJSON)
        }

        return parameterArray
    }

    fun makeAgencyListParameter(loginID: String, searchTXT: String, selectAgency : String, selection : String): JSONArray {
        var parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = selection
        parameterJSON["CenterId"] = selectAgency
        parameterJSON["InputData"] = searchTXT
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeAgencyAddrListParameter(loginID: String, Type: String, CenterId : String, AgencyId : String): JSONArray {
        var parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = Type
        parameterJSON["CallCenterId"] = CenterId
        parameterJSON["AgencyId"] = AgencyId
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeRiderInfoListParameter(loginID: String, rType: String, type : String, CenterId : String, Sort : String ,Work : String, Keyword : String): JSONArray {
        var parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = rType
        parameterJSON["CallCenterId"] = CenterId
        parameterJSON["TYPE"] = type
        parameterJSON["ORDER_BY_TYPE"] = Sort
        parameterJSON["WORK_SEARCHING_TYPE"] = Work
        parameterJSON["CENTER_ID"] = CenterId
        parameterJSON["KEYWORD"] = Keyword
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeDeliveryFeeInfoParameter(loginID: String, Type: String, AgencyId : String, BuldingNo : String, Lat : String, Lon : String, TownCode : String): JSONArray {
        var parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = Type
        parameterJSON["AgencyId"] = AgencyId
        parameterJSON["CustomerBuildingManageNo"] = BuldingNo
        parameterJSON["CustomerLatitude"] = Lat
        parameterJSON["CustomerLongitude"] = Lon
        parameterJSON["CustomerLawTownCode"] = TownCode
        parameterArray.add(parameterJSON)

        return parameterArray
    }

    fun makeNewOrderRegParameter(loginID: String, Type: String, Regdata : HashMap<String,String>): JSONArray {
        var parameterArray = JSONArray()

        val parameterJSON = makeBaseParameter(loginID)
        parameterJSON["ReqType"] = Type
        parameterJSON["INPUT_TYPE"] = Regdata["INPUT_TYPE"]
        parameterJSON["RECEIPT_ID"] = Regdata["RECEIPT_ID"]
        parameterJSON["REG_ID_TYPE"] = Regdata["REG_ID_TYPE"]
        parameterJSON["USER_ID"] = Regdata["USER_ID"]
        parameterJSON["ORDER_CLASS"] = Regdata["ORDER_CLASS"]
        parameterJSON["DELIVERY_PLAN_DT"] = Regdata["DELIVERY_PLAN_DT"]
        parameterJSON["AGENCY_ID"] = Regdata["AGENCY_ID"]
        parameterJSON["CUSTOMER_ID"] = Regdata["CUSTOMER_ID"]
        parameterJSON["CUSTOMERN_AME"] = Regdata["CUSTOMERN_AME"]
        parameterJSON["CUSTOMER_PHONE"] = Regdata["CUSTOMER_PHONE"]
        parameterJSON["CUSTOMER_MOBILE"] = Regdata["CUSTOMER_MOBILE"]
        parameterJSON["CUSTOMER_ADDR"] = Regdata["CUSTOMER_ADDR"] + Regdata["ShortLoadAddress"]
        parameterJSON["CUSTOMER_LAWTOWNCODE"] = Regdata["CUSTOMER_LAWTOWNCODE"]
        parameterJSON["CUSTOMER_LAWTOWNNAME"] = Regdata["CUSTOMER_LAWTOWNNAME"]
        parameterJSON["CUSTOMER_JIBUN"] = Regdata["CUSTOMER_JIBUN"]
        parameterJSON["CUSTOMER_ADDR_DETAIL"] = Regdata["CUSTOMER_ADDR_DETAIL"]
        parameterJSON["CUSTOMER_BUILDING_MANAGE_NO"] = Regdata["CUSTOMER_BUILDING_MANAGE_NO"]
        parameterJSON["APPROVAL_TYPE"] = Regdata["APPROVAL_TYPE"]
        parameterJSON["PRICE"] = Regdata["PRICE"]
        parameterJSON["ORDER_DESC"] = Regdata["ORDER_DESC"]
        parameterJSON["CONTENT"] = Regdata["CONTENT"]
        parameterJSON["PICKUP_YN"] = Regdata["PICKUP_YN"]
        parameterJSON["CANCEL_RESON"] = Regdata["CANCEL_RESON"]
        parameterJSON["CUSTOMER_LATITUDE"] = Regdata["CUSTOMER_LATITUDE"]
        parameterJSON["CUSTOMER_LONGITUDE"] = Regdata["CUSTOMER_LONGITUDE"]
        parameterJSON["ORDER_DELAY"] = Regdata["ORDER_DELAY"]
        parameterJSON["ShortLoadAddress"] = Regdata["ShortLoadAddress"]
        parameterArray.add(parameterJSON)

        return parameterArray
    }

//    fun makeAlarmAcknowledgementParameter(alarmID: String): JSONArray {
//        val parameterJSON = JSONObject()
//
//        val parameterArray = JSONArray()
//        parameterJSON["AppType"] = appType
//        parameterJSON["DeviceId"] = globalInfo.deviceID
//        parameterJSON["AppId"] = globalInfo.appID
//        parameterJSON["Type"] = "Received2"
//        parameterJSON["AlarmIds"] = alarmID
//        parameterJSON["Localtime"] = TimeSync.getCurrentTimeString()
//
//        parameterArray.add(parameterJSON)
//
//        return parameterArray
//    }
}