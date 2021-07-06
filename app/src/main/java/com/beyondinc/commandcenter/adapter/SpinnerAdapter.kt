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
}