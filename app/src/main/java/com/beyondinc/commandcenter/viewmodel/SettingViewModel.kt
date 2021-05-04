package com.beyondinc.commandcenter.viewmodel

import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.SettingFun
import com.beyondinc.commandcenter.activity.Setting
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars

class SettingViewModel : ViewModel() {

    var usenick : MutableLiveData<Boolean> = MutableLiveData()
    var useTime : MutableLiveData<Boolean> = MutableLiveData()
    var useGana :MutableLiveData<Boolean> = MutableLiveData()
    var useTTS : MutableLiveData<Boolean> = MutableLiveData()
    var useJ : MutableLiveData<Boolean> = MutableLiveData()
    var useB : MutableLiveData<Boolean> = MutableLiveData()
    var useW : MutableLiveData<Boolean> = MutableLiveData()
    var useC : MutableLiveData<Boolean> = MutableLiveData()
    var bright : MutableLiveData<Int> = MutableLiveData()
    var nick : MutableLiveData<HashMap<String,String>> = MutableLiveData()

    init
    {
        usenick.postValue(Vars.Usenick)
        useTime.postValue(Vars.UseTime)
        useGana.postValue(Vars.UseGana)
        useTTS.postValue(Vars.UseTime)
        useJ.postValue(Vars.UseJ)
        useB.postValue(Vars.UseB)
        useW.postValue(Vars.UseW)
        useC.postValue(Vars.UseC)
        bright.postValue(Vars.Bright)
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
        ed.apply()

        Vars.MainsHandler!!.obtainMessage(Finals.SET_BRIGHT).sendToTarget()

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
        usenick.postValue(true)
    }

    fun click_nick_un_use(){
        usenick.postValue(false)
    }

    fun click_order_use(){
        useTime.postValue(true)
    }

    fun click_order_un_use(){
        useTime.postValue(false)
    }

    fun click_rider_use(){
        useGana.postValue(true)
    }

    fun click_rider_un_use(){
        useGana.postValue(false)
    }

    fun click_j_use(){
        if(useJ.value == false) useJ.postValue(true)
        else useJ.postValue(false)
    }

    fun click_b_use(){
        if(useB.value == false) useB.postValue(true)
        else useB.postValue(false)
    }

    fun click_w_use(){
        if(useW.value == false) useW.postValue(true)
        else useW.postValue(false)
    }

    fun click_c_use(){
        if(useC.value == false) useC.postValue(true)
        else useC.postValue(false)
    }

    fun click_tts_use(){
        useTTS.postValue(true)
    }

    fun click_tts_un_use(){
        useTTS.postValue(false)
    }

    fun click_brigth_plus(){
        if(bright.value!! < 10) {
            var i = bright.value!! + 1
            bright.postValue(i)
        }
    }

    fun click_brigth_minus(){
        if(bright.value!! > 0) {
            var i = bright.value!! - 1
            bright.postValue(i)
        }
    }
}