package com.beyondinc.commandcenter.adapter

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.text.DecimalFormat


object SpinnerAdapter {

    @JvmStatic
    @BindingAdapter("DeliveryExtFeeType")
    fun DeliveryExtFeeType(view: Spinner, velue: String) {
        if(velue == "7401")view.setSelection(0)
        else if(velue == "7402")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("DepositYn")
    fun DepositYn(view: Spinner, velue: String) {
        if(velue == "Y")view.setSelection(0)
        else if(velue == "N")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("ShareOrderYn")
    fun ShareOrderYn(view: Spinner, velue: String) {
        if(velue == "Y")view.setSelection(0)
        else if(velue == "N")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("State")
    fun State(view: Spinner, velue: String) {
        if(velue == "2801")view.setSelection(0)
        else if(velue == "2802")view.setSelection(1)
        else if(velue == "2803")view.setSelection(2)
    }

    @JvmStatic
    @BindingAdapter("AgencyColor")
    fun AgencyColor(view: Spinner, velue: String) {
        //이거 언제구현하지요?
    }

    @JvmStatic
    @BindingAdapter("AgencyWorkState")
    fun AgencyWorkState(view: Spinner, velue: String) {
        if(velue == "0")view.setSelection(0)
        else if(velue == "1")view.setSelection(1)
        else if(velue == "2")view.setSelection(2)
    }

    @JvmStatic
    @BindingAdapter("AgencyMonthlyOrderLimitYn")
    fun AgencyMonthlyOrderLimitYn(view: Spinner, velue: String) {
        if(velue == "N")view.setSelection(0)
        else if(velue == "Y")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("AgencyDailyOrderLimitYn")
    fun AgencyDailyOrderLimitYn(view: Spinner, velue: String) {
        if(velue == "N")view.setSelection(0)
        else if(velue == "Y")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("SurchargeByAgencySettingYn")
    fun SurchargeByAgencySettingYn(view: Spinner, velue: String) {
        if(velue == "Y")view.setSelection(0)
        else if(velue == "N")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("WarningDepositByAgencySettingYn")
    fun WarningDepositByAgencySettingYn(view: Spinner, velue: String) {
        if(velue == "Y")view.setSelection(0)
        else if(velue == "N")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("MinDepositByAgencySettingYn")
    fun MinDepositByAgencySettingYn(view: Spinner, velue: String) {
        if(velue == "Y")view.setSelection(0)
        else if(velue == "N")view.setSelection(1)
    }

    @JvmStatic
    @BindingAdapter("DeliveryExtFeeType1")
    fun onItemSelectedDeliveryExtFeeType1(view : TextView, value : String) {
        var result : String = ""
        if(value == "7401") result = "구역할증"
        else if(value  == "7402") result = "동별할증"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("DepositYn1")
    fun onItemSelectedDepositYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "Y") result = "사용"
        else if(value  == "N") result = "미사용"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("ShareOrderYn1")
    fun onItemSelectedShareOrderYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "Y") result = "사용"
        else if(value == "N") result = "미사용"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("State1")
    fun onItemSelectedState1(view : TextView, value : String) {
        var result : String = ""
        if(value == "2801") result = "사용"
        else if(value == "2802") result = "미사용"
        else if(value == "2803") result = "해지"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("AgencyColor1")
    fun onItemSelectedAgencyColor1(view : TextView, value : String) {
        var result : String = "미적용"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("AgencyWorkState1")
    fun onItemSelectedAgencyWorkState1(view : TextView, value : String) {
        var result : String = ""
        if(value == "0") result = "정상업무"
        else if(value == "1") result = "일시정지"
        else if(value == "2") result = "업무종료"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("AgencyMonthlyOrderLimitYn1")
    fun onItemSelectedAgencyMonthlyOrderLimitYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "N") result = "월 제한없음"
        else if(value == "Y") result = "월 건수 제한"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("AgencyDailyOrderLimitYn1")
    fun onItemSelectedAgencyDailyOrderLimitYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "N") result = "일 제한없음"
        else if(value == "Y") result = "일 건수 제한"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("SurchargeByAgencySettingYn1")
    fun onItemSelectedSurchargeByAgencySettingYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "Y") result = "가맹점 설정 적용"
        else if(value == "N") result = "센터 설정 적용"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("WarningDepositByAgencySettingYn1")
    fun onItemSelectedWarningDepositByAgencySettingYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "Y") result = "가맹점 설정 적용"
        else if(value == "N") result = "센터 설정 적용"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("MinDepositByAgencySettingYn1")
    fun onItemSelectedMinDepositByAgencySettingYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "Y") result = "가맹점 설정 적용"
        else if(value == "N") result = "센터 설정 적용"
        view.text = result
    }


    // 라이더 어뎁터

    @JvmStatic
    @BindingAdapter("state1")
    fun onItemSelectedstate1(view : TextView, value : String) {
        var result : String = ""
        if(value == "1001") result = "재직"
        else if(value  == "1002") result = "퇴사"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("taxPaymentTypeId1")
    fun onItemSelectedtaxPaymentTypeId1(view : TextView, value : String) {
        var result : String = ""
        if(value == "7801") result = "미적용"
        else if(value  == "7802") result = "원천징수"
        else if(value  == "7803") result = "종합소득세"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("recvAlarmDelayId1")
    fun onItemSelectedrecvAlarmDelayId1(view : TextView, value : String) {
        var result : String = ""
        if(value == "5700") result = "지연안함"
        else if(value == "5702") result = "2초"
        else if(value == "5703") result = "3초"
        else if(value == "5705") result = "5초"
        else if(value == "5710") result = "10초"
        else if(value == "5720") result = "20초"
        else if(value == "5730") result = "30초"
        else if(value == "5760") result = "1분"
        else if(value == "5701") result = "2분"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("showOrderStatusYn1")
    fun onItemSelectedshowOrderStatusYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "Y") result = "가능"
        else if(value == "N") result = "불가능"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("shareOrderViewTypeId1")
    fun onItemSelectedshareOrderViewTypeId1(view : TextView, value : String) {
        var result : String = ""
        if(value == "7701") result = "전체보기"
        else if(value == "7702") result = "전체안보기"
        else if(value == "7703") result = "선택보기"
        view.text = result
    }

    @JvmStatic
    @BindingAdapter("assignCancelByRiderYnByRiderSettingYn1")
    fun onItemSelectedassignCancelByRiderYnByRiderSettingYn1(view : TextView, value : String) {
        var result : String = ""
        if(value == "Y") result = "라이더 설정 적용"
        else if(value == "N") result = "센터 설정 적용"
        view.text = result
    }
}