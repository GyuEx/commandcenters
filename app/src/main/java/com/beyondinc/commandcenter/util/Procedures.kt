package com.beyondinc.commandcenter.util

object Procedures {

    const val LOGIN = "dim_spLogin"
    const val LOGOUT = LOGIN
    const val CHANGE_PASSWORD = LOGIN

    object UserAuthType {
        const val LOGIN = "Login"
        const val LOGOUT = "Logout"
        const val CHANGE_PASSWORD = "Change.Passwd"
    }

    const val ALARM_ACKNOWLEDGEMENT = "dim_AlarmAck"

    const val CENTER_LIST = "dim_spAccessibleCenterList"   // 센터 전체 리스트 가져오기
    const val RIDER_LIST_IN_CENTER = "dim_spRiderByCenter" // 센터별 라이더 목록 조회
    const val ORDER_LIST_IN_CENTER = "dim_spMgrOrderList"  // 센터별 오더목록 조회
    const val AGENCY_LIST = "dim_spAgencyByCenter"         // 센터별 가맹점 조회
    const val AGENCY_TOWN_LIST = "dim_spAgencyLocalAddr" // 가맹점별 배달가능지역 조회
    const val DELIVERY_FEE_INFO = "dim_spDeliveryFeeInfo" // 배달금액 조회
    const val REG_NEW_ORDER = "dim_spOrderReg"
    const val RIDER_LIST = "dim_spMgrRiderListInfo"

    object AgencyTownType {
        const val SearchDong = "SearchCallcenterTown"
    }
    object DeliveryFeeType {
        const val SearchFee = "SearchDeliveryFeeInfo"
    }
    object RegOrderType {
        const val NewOrder = "RegNewOrder"
    }

    // TODO: 실제 오더상세 수신이 아닌 알람 수신시 개별오더 업데이트용. 이름을 변경해야 함
//    const val ORDER_DETAIL = "dim_spMgrOrderDetail"
    const val ORDER_DETAIL = "dim_spMgrOrderList"

    const val CHANGE_DELIVERY_STATUS = "dim_spDeliveryStatusChange"

    object ChangeStatusType {
        const val ASSIGN_RIDER = "Rider.Assign"
        const val CHANGE_RIDER = "Rider.Change"
        const val ORDER_ASSIGN_CANCEL = "Rider.AssignCancel"
        const val ORDER_FORCE_COMPLETE = "Order.Complete"
        const val ORDER_CANCEL = "Order.Cancel"
    }

    const val EDIT_ORDER_INFO = "dim_spOrderDetailEdit"

    object EditInfoType {
        const val AGENCY_TOWN_LIST = "Req.AgencyTown"
        const val SEARCH_ADDRESS = "Req.SearchAddr"
        const val CHANGE_DELIVERY_ADDRESS = "Req.CustomerAddrChange"
        const val ORDER_PACKING_COMPLETE = "Req.PackingComplete"
        const val CHANGE_SALES_PRICE = "Req.SalesPriceChange"
        const val ADD_DELIVERY_FEE = "Req.ChangeAddDeliveryFee"
        const val APPROVAL_TYPE = "Req.Approval"
    }

    const val RIDER_LOCATION_IN_CENTER = "dim_spRiderLastLocationInfo"
}