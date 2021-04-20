package com.beyondinc.commandcenter.viewmodel
import android.os.Handler
import android.os.Message
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.util.Vars

class LoginsViewModel() : ViewModel() {

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                (Vars.mContext as LoginsFun).LoginSuccess()
            }
        }
    }

    var isLogin = false
    //val mainthread = MainThread(handler)

    fun Logins() {
        if(isLogin == false)
        {
            (Vars.mContext as LoginsFun).LoginSuccess()
//            mainthread.start()
            isLogin = true
        }
    }
}