package com.beyondinc.commandcenter.viewmodel

import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.SettingFun
import com.beyondinc.commandcenter.activity.Setting
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.concurrent.ConcurrentHashMap

class SettingViewModel : ViewModel() {

    var usenick : MutableLiveData<Boolean> = MutableLiveData() // 닉네임 사용여부
    var useTime : MutableLiveData<Boolean> = MutableLiveData() // 시간정렬 사용여부
    var useGana :MutableLiveData<Boolean> = MutableLiveData() // 라이더 가나다순 정렬사용여부
    var useTTS : MutableLiveData<Boolean> = MutableLiveData() // TTS사용 여부
    var useJ : MutableLiveData<Boolean> = MutableLiveData() // 접수안내 사용여부
    var useB : MutableLiveData<Boolean> = MutableLiveData() // 배정안내
    var useW : MutableLiveData<Boolean> = MutableLiveData() // 완료안내
    var useC : MutableLiveData<Boolean> = MutableLiveData() // 취소안내
    var bright : MutableLiveData<Int> = MutableLiveData() // 화면 밝기
    var fontsize : MutableLiveData<Int> = MutableLiveData() // 글씨크기

    init
    {
        usenick.value = Vars.Usenick
        useTime.value = Vars.UseTime
        useGana.value = Vars.UseGana
        useTTS.value = Vars.UseTTS
        useJ.value = Vars.UseJ
        useB.value = Vars.UseB
        useW.value = Vars.UseW
        useC.value = Vars.UseC
        bright.value = Vars.Bright
        fontsize.value = Vars.FontSize
    }

    fun click_save(){

        Vars.Usenick = usenick.value!!
        Vars.UseTime = useTime.value!!
        Vars.UseGana = useGana.value!!
        Vars.UseTTS = useTTS.value!!
        Vars.UseJ = useJ.value!!
        Vars.UseB = useB.value!!
        Vars.UseW = useW.value!!
        Vars.UseC = useC.value!!
        Vars.Bright = bright.value!!
        Vars.FontSize = fontsize.value!!

        var pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
        var ed = pref.edit()
        ed.putBoolean("usenick", usenick.value!!)
        ed.putBoolean("useTime", useTime.value!!)
        ed.putBoolean("useGana", useGana.value!!)
        ed.putBoolean("useTTS", useTTS.value!!)
        ed.putBoolean("useJ", useJ.value!!)
        ed.putBoolean("useB", useB.value!!)
        ed.putBoolean("useW", useW.value!!)
        ed.putBoolean("useC", useC.value!!)
        ed.putInt("bright", bright.value!!)
        ed.putInt("fontsize", fontsize.value!!)
        ed.apply()

        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.SET_BRIGHT,0).sendToTarget()

        exit()
    }

    fun click_nick_setting(){
        (Vars.sContext as SettingFun).showDialog()
    }

    fun click_close_pop(){
        (Vars.sContext as SettingFun).closeDialog()
    }

    fun click_cancel(){
        exit()
    }

    fun exit(){
        (Vars.sContext as SettingFun).Exit()
    }

    fun click_nick_use(){
        usenick.value = true
    }

    fun click_nick_un_use(){
        usenick.value = false
    }

    fun click_order_use(){
        useTime.value = true
    }

    fun click_order_un_use(){
        useTime.value = false
    }

    fun click_rider_use(){
        useGana.value = true
    }

    fun click_rider_un_use(){
        useGana.value = false
    }

    fun click_j_use(){
        useJ.value = useJ.value == false
    }

    fun click_b_use(){
        useB.value = useB.value == false
    }

    fun click_w_use(){
        useW.value = useW.value == false
    }

    fun click_c_use(){
        useC.value = useC.value == false
    }

    fun click_tts_use(){
        useTTS.value = true
    }

    fun click_tts_un_use(){
        useTTS.value = false
    }

    fun click_brigth_plus(){
        if(bright.value!! < 10) {
            var i = bright.value!! + 1
            bright.value = i
        }
    }

    fun click_brigth_minus(){
        if(bright.value!! > 0) {
            var i = bright.value!! - 1
            bright.value = i
        }
    }

    fun click_fontsize_plus(){
        if(fontsize.value!! < 30) {
            var i = fontsize.value!! + 1
            fontsize.value = i
        }
    }

    fun click_fontsize_minus(){
        if(fontsize.value!! > 1) {
            var i = fontsize.value!! - 1
            fontsize.value = i
        }
    }
}