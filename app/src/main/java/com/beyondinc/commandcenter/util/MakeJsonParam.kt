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

//    fun makeLogoutParameter(loginID: String): JSONArray {
//        val parameterArray = JSONArray()
//
//        val parameterJSON = makeBaseParameter(loginID)
//        parameterJSON["ReqType"] = Procedures.UserAuthType.LOGOUT
//        parameterJSON["ModelName"] = globalInfo.deviceModelNumber
//        parameterArray.add(parameterJSON)
//
//        return parameterArray
//    }

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