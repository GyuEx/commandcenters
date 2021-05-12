package com.beyondinc.commandcenter.viewmodel
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.text.SimpleDateFormat
import java.util.*

open class LoginViewModel() : ViewModel() {

    var id : MutableLiveData<String> = MutableLiveData()
    var pw : MutableLiveData<String> = MutableLiveData()

    var saveId : MutableLiveData<Boolean> = MutableLiveData()
    var savePw : MutableLiveData<Boolean> = MutableLiveData()

    init {
        Logindata.isLogin = false

        saveId.value = false
        savePw.value = false

        val pref = PreferenceManager.getDefaultSharedPreferences(Vars.lContext)
        if(pref.getBoolean("saveid",false))
        {
            saveId.value = true
            id.value = pref.getString("id","")
        }
        if(pref.getBoolean("savepw",false))
        {
            savePw.value = true
            pw.value = pref.getString("pw","")
        }

        getPhoneNum()

        Vars.LoginHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == Finals.LOGIN_SUCESS)
                {
                    (Vars.lContext as LoginsFun).LoginSuccess()
                }
                else if (msg.what == Finals.LOGIN_FAIL)
                {
                    if(Logindata.MSG == null)
                    {
                        var toast : Toast = Toast.makeText(Vars.lContext, "서버접속실패, 다시 시도해주세요.", Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.TOP,0,300)
                            toast.show()
                    }
                    else
                    {
                        var toast : Toast = Toast.makeText(Vars.lContext, Logindata.MSG, Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.TOP,0,300)
                        toast.show()
                    }
                    (Vars.lContext as LoginsFun).LoginFail()
                }
            }
        }
    }

    fun Login() {
        if(id.value.isNullOrEmpty()) Toast.makeText(
            Vars.lContext,
            "아이디를 입력해주세요.",
            Toast.LENGTH_SHORT
        ).show()
        else if(pw.value.isNullOrEmpty()) Toast.makeText(
            Vars.lContext,
            "비밀번호를 입력해주세요.",
            Toast.LENGTH_SHORT
        ).show()
        else
        {
            Logindata.LoginId = id.value
            Logindata.LoginPw = pw.value //굳이 비밀번호를 저장할 필요가 있을까?

            (Vars.lContext as LoginsFun).Login(id.value!!, pw.value!!)
            if(saveId.value == true) {
                var pref = PreferenceManager.getDefaultSharedPreferences(Vars.lContext)
                var ed = pref.edit()
                ed.putString("id", id.value)
                ed.putBoolean("saveid", saveId.value!!)
                ed.apply()
            }
            if(savePw.value == true) {
                var pref = PreferenceManager.getDefaultSharedPreferences(Vars.lContext)
                var ed = pref.edit()
                ed.putString("pw", pw.value)
                ed.putBoolean("savepw", savePw.value!!)
                ed.apply()
            }
        }
    }

    fun getTime() : String? {
        val now: Long = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd (EE)", Locale("ko", "KR"))
        return dateFormat.format(date)
    }

    fun saveId(){
        saveId.value = saveId.value != true
        var pref = PreferenceManager.getDefaultSharedPreferences(Vars.lContext)
        var ed = pref.edit()
        ed.putBoolean("saveid", saveId.value!!)
        ed.apply()
    }

    fun savePw(){
        savePw.value = savePw.value != true
        var pref = PreferenceManager.getDefaultSharedPreferences(Vars.lContext)
        var ed = pref.edit()
        ed.putBoolean("savepw", savePw.value!!)
        ed.apply()
    }

    fun getPhoneNum(){
        var tel : TelephonyManager = Vars.lContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(Vars.lContext!!, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(Vars.lContext!!, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(Vars.lContext!!, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        Logindata.devicePhone = tel.line1Number.toString()
        Logindata.deviceModel = Build.MODEL
        Log.e("aaaa", "" + Logindata.devicePhone + " // " + Logindata.deviceModel)
    }
}