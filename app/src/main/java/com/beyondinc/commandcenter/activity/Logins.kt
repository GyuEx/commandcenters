package com.beyondinc.commandcenter.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.databinding.ActivityLoginsBinding
import com.beyondinc.commandcenter.handler.AlarmThread
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.net.HttpConn
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.MakeJsonParam
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.LoginViewModel
import com.kakao.util.helper.Utility.getPackageInfo
import org.json.simple.JSONArray
import java.security.MessageDigest
import java.util.*
import kotlin.collections.HashMap

class Logins : AppCompatActivity() , LoginsFun {
    var binding: ActivityLoginsBinding? = null
    var viewModel: LoginViewModel? = null
    var isLogin = false

    private val Tag = "Logins Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.lContext = this

        checkpermission()

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
        if(Vars.AlarmThread == null)
        {
            Vars.AlarmThread = AlarmThread()
            Vars.AlarmThread!!.start()
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
            temp[Procedures.LOGIN] = MakeJsonParam().makeLoginParameter(id, pw)
            Vars.sendList.add(temp)
            isLogin = true
        }
    }

    override fun LoginSuccess() {
        Logindata.isLogin = isLogin
        startActivity(Intent(Vars.lContext, Mains::class.java))
        finish()
    }

    override fun LoginFail() {
        isLogin = false
    }

    fun checkpermission(){
        if (Build.VERSION.SDK_INT >= 17) {
            val pms: MutableList<String> = ArrayList()
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.READ_PHONE_STATE
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.CALL_PHONE
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.RECORD_AUDIO
            )
            if (pms.size == 0) {

            } else ActivityCompat.requestPermissions(
                this,
                pms.toTypedArray(), Finals.REQUEST_PERMISSION
            )
        }
    }
}