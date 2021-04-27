package com.beyondinc.commandcenter.viewmodel
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.text.SimpleDateFormat
import java.util.*

class LoginViewModel() : ViewModel() {

    var id : MutableLiveData<String> = MutableLiveData()
    var pw : MutableLiveData<String> = MutableLiveData()

    var saveId : MutableLiveData<Boolean> = MutableLiveData()
    var savePw : MutableLiveData<Boolean> = MutableLiveData()

    init {
        Logindata.isLogin = false

        saveId.postValue(false)
        savePw.postValue(false)

        val pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
        if(pref.getBoolean("saveid",false))
        {
            saveId.postValue(true)
            id.postValue(pref.getString("id",""))
        }
        if(pref.getBoolean("savepw",false))
        {
            savePw.postValue(true)
            pw.postValue(pref.getString("pw",""))
        }

        Vars.LoginHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == Finals.LOGIN_SUCESS)
                {
                    (Vars.mContext as LoginsFun).LoginSuccess()
                }
                else if (msg.what == Finals.LOGIN_FAIL)
                {
                    if(Logindata.MSG == null) Toast.makeText(Vars.mContext, "서버접속실패, 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(Vars.mContext, Logindata.MSG, Toast.LENGTH_SHORT).show()
                    (Vars.mContext as LoginsFun).LoginFail()
                }
            }
        }
    }

    fun Login() {
        if(id.value.isNullOrEmpty()) Toast.makeText(
            Vars.mContext,
            "아이디를 입력해주세요.",
            Toast.LENGTH_SHORT
        ).show()
        else if(pw.value.isNullOrEmpty()) Toast.makeText(
            Vars.mContext,
            "비밀번호를 입력해주세요.",
            Toast.LENGTH_SHORT
        ).show()
        else
        {
            Logindata.LoginId = id.value
            Logindata.LoginPw = pw.value //굳이 비밀번호를 저장할 필요가 있을까?

            (Vars.mContext as LoginsFun).Login(id.value!!, pw.value!!)
            if(saveId.value == true) {
                var pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
                var ed = pref.edit()
                ed.putString("id", id.value)
                ed.putBoolean("saveid", saveId.value!!)
                ed.apply()
            }
            if(savePw.value == true) {
                var pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
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
        var pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
        var ed = pref.edit()
        ed.putBoolean("saveid", saveId.value!!)
        ed.apply()
    }

    fun savePw(){
        savePw.value = savePw.value != true
        var pref = PreferenceManager.getDefaultSharedPreferences(Vars.mContext)
        var ed = pref.edit()
        ed.putBoolean("savepw", savePw.value!!)
        ed.apply()
    }
}