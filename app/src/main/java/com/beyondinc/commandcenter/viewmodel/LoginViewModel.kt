package com.beyondinc.commandcenter.viewmodel
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars

class LoginViewModel() : ViewModel() {

    val id : MutableLiveData<String> = MutableLiveData()
    val pw : MutableLiveData<String> = MutableLiveData()

    init {
        Logindata.isLogin = false
        id.postValue("commandcenter")
        pw.postValue("1111")

        Log.e("Lonins","" + Vars.LoginHandler)
        Vars.LoginHandler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == Finals.LOGIN_SUCESS)
                {
                    (Vars.mContext as LoginsFun).LoginSuccess()
                }
                else if (msg.what == Finals.LOGIN_FAIL)
                {
                    Toast.makeText(Vars.mContext,Logindata.MSG, Toast.LENGTH_SHORT).show()
                    (Vars.mContext as LoginsFun).LoginFail()
                }
            }
        }
    }

    fun Login() {
        if(id.value.isNullOrEmpty()) Toast.makeText(Vars.mContext,"아이디를 입력해주세요.",Toast.LENGTH_SHORT).show()
        else if(pw.value.isNullOrEmpty()) Toast.makeText(Vars.mContext,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
        else (Vars.mContext as LoginsFun).Login(id.value!!,pw.value!!)
    }
}