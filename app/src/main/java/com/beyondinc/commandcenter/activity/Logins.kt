package com.beyondinc.commandcenter.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.databinding.ActivityLoginsBinding
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.net.HttpConn
import com.beyondinc.commandcenter.util.MakeJsonParam
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.LoginViewModel
import org.json.simple.JSONArray

class Logins : AppCompatActivity() , LoginsFun {
    var binding: ActivityLoginsBinding? = null
    var viewModel: LoginViewModel? = null
    var isLogin = false

    private val Tag = "Logins Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.mContext = this

        if(Vars.MainThread == null)
        {
            Vars.MainThread = MainThread()
            Vars.MainThread!!.start()
        }
        if(Vars.HttpThread == null)
        {
            Vars.HttpThread = HttpConn()
            Vars.HttpThread!!.start()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_logins)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        Log.e("OnCreate","OnStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(Tag, "Destory")
    }

    override fun Login(id : String, pw: String) {
        if (!isLogin) {
            var temp: HashMap<String, JSONArray> = HashMap()
            temp.put(Procedures.LOGIN, MakeJsonParam().makeLoginParameter(id, pw))
            Vars.sendList.add(temp)
            isLogin = true
        }
    }

    override fun LoginSuccess() {
        Logindata.isLogin = isLogin
        startActivity(Intent(Vars.mContext, Mains::class.java))
        finish()
    }

    override fun LoginFail() {
        isLogin = false
    }
}