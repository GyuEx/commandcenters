package com.beyondinc.commandcenter.viewmodel

import android.preference.PreferenceManager
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.Interface.SettingFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.regex.Matcher
import java.util.regex.Pattern


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
    var rate : MutableLiveData<Float> = MutableLiveData() // tts속도
    var tone : MutableLiveData<Float> = MutableLiveData() // tts톤
    var rateinfo : MutableLiveData<String> = MutableLiveData() // 속도안내
    var toneinfo : MutableLiveData<String> = MutableLiveData() // 톤안내
    var rateseek : MutableLiveData<Int> = MutableLiveData() // 시크바값
    var toneseek : MutableLiveData<Int> = MutableLiveData() // 시크바값

    init
    {
        Log.e("Setting","세팅 뷰모델이 나타났습니다.")
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
        rate.value = Vars.speechrate
        tone.value = Vars.speechpitch
        rateinfo.value = Vars.speechrateinfo
        toneinfo.value = Vars.speechpitchinfo
        rateseek.value = Vars.rateseek
        toneseek.value = Vars.toneseek

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
        Vars.speechrate = rate.value!!
        Vars.speechpitch = tone.value!!
        Vars.speechrateinfo = rateinfo.value!!
        Vars.speechpitchinfo = toneinfo.value!!
        Vars.rateseek = rateseek.value!!
        Vars.toneseek = toneseek.value!!

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
        ed.putFloat("rate",rate.value!!)
        ed.putFloat("tone",tone.value!!)
        ed.putString("rateinfo",rateinfo.value!!)
        ed.putString("toneinfo",toneinfo.value!!)
        ed.putInt("rateseek",rateseek.value!!)
        ed.putInt("toneseek",toneseek.value!!)
        ed.apply()

        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN, Finals.SET_BRIGHT, 0).sendToTarget()

        exit()
    }

    fun click_nick_setting(){
        (Vars.sContext as SettingFun).showDialog()
    }

    fun click_change_password(){
        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.RE_LOGIN,0).sendToTarget()
        closeEmer()
        exit()
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

    fun onProgressChanged1(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when(progress)
        {
            0 -> {rate.value = 0.1f
                rateinfo.value = "매우느림"}
            1 -> {rate.value = 0.2f
                rateinfo.value = "아주느림"}
            2 -> {rate.value = 0.4f
                rateinfo.value = "느림"}
            3 -> {rate.value = 0.6f
                rateinfo.value = "느림"}
            4 -> {rate.value = 0.8f
                rateinfo.value = "조금느림"}
            5 -> {rate.value = 1.0f
                rateinfo.value = "보통"}
            6 -> {rate.value = 1.2f
                rateinfo.value = "조금빠름"}
            7 -> {rate.value = 1.4f
                rateinfo.value = "빠름"}
            8 -> {rate.value = 1.6f
                rateinfo.value = "빠름"}
            9 -> {rate.value = 1.8f
                rateinfo.value = "아주빠름"}
            10 -> {rate.value = 2.0f
                rateinfo.value = "매우빠름"}
        }
    }

    fun onProgressChanged2(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when(progress)
        {
            0 -> {tone.value = 0.1f
                toneinfo.value = "매우낮음"}
            1 -> {tone.value = 0.2f
                toneinfo.value = "아주낮음"}
            2 -> {tone.value = 0.4f
                toneinfo.value = "낮음"}
            3 -> {tone.value = 0.6f
                toneinfo.value = "낮음"}
            4 -> {tone.value = 0.8f
                toneinfo.value = "조금낮음"}
            5 -> {tone.value = 1.0f
                toneinfo.value = "보통"}
            6 -> {tone.value = 1.2f
                toneinfo.value = "조금높음"}
            7 -> {tone.value = 1.4f
                toneinfo.value = "높음"}
            8 -> {tone.value = 1.6f
                toneinfo.value = "높음"}
            9 -> {tone.value = 1.8f
                toneinfo.value = "아주높음"}
            10 -> {tone.value = 2.0f
                toneinfo.value = "매우높음"}
        }
    }

    fun showEmer(){
        (Vars.sContext as SettingFun).showEmer()
    }

    fun closeEmer(){
        (Vars.sContext as SettingFun).closeDialog()
    }
}