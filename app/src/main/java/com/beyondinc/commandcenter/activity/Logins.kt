package com.beyondinc.commandcenter.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.databinding.ActivityLoginsNewBinding
import com.beyondinc.commandcenter.fragment.ChangePasswordDialog
import com.beyondinc.commandcenter.fragment.DownloadingDialog
import com.beyondinc.commandcenter.fragment.LoginLoadingDialog
import com.beyondinc.commandcenter.fragment.MsgDialog
import com.beyondinc.commandcenter.handler.*
import com.beyondinc.commandcenter.net.HttpConn
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.MakeJsonParam
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.LoginViewModel
import org.json.simple.JSONArray
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.exitProcess


class Logins : AppCompatActivity() , LoginsFun {
    var binding: ActivityLoginsNewBinding? = null
    var isLogin = false
    var loading:LoginLoadingDialog? = null
    var downloading:DownloadingDialog? = null
    var password : ChangePasswordDialog? = null

    var msgDialog : MsgDialog? = null

    private val Tag = "Logins Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.lContext = this

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_logins_new)
        if(Vars.LoginVm == null) Vars.LoginVm = LoginViewModel()
        binding!!.lifecycleOwner = this
        binding!!.viewModel = Vars.LoginVm

        if(intent.hasExtra("ReLogin")){
            showPassword(Logindata.LoginPw.toString(),"변경할 비밀번호를 입력해주세요")
        }
        else if(intent.hasExtra("Expire")){
            Vars.LoginVm?.ExpireMessage!!.value = "장시간 사용하지 않아\n시스템을 종료 합니다"
            showMsg()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun Login(id: String, pw: String) {
        if (!isLogin) {
            var temp: ConcurrentHashMap<String, JSONArray> = ConcurrentHashMap()
            temp[Procedures.LOGIN] = MakeJsonParam().makeLoginParameter(id, pw)
            Vars.sendList.add(temp)
            isLogin = true
        }
    }

    override fun LoginSuccess() {
        Logindata.isLogin = isLogin
        setResult(RESULT_OK)
        finish()
    }

    override fun LoginFail() {
        closeLoading()
        isLogin = false
    }

    override fun onDestroy() {
        super.onDestroy()
        Vars.LoginVm = null
    }

    override fun showLoading() {
        try {
            runOnUiThread {
                if (loading != null) {
                    loading!!.dismiss()
                    loading = null
                }
                loading = LoginLoadingDialog()
                loading!!.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BBB)
                loading!!.show(supportFragmentManager, "Loading")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showMsg() {
        try {
            runOnUiThread {
                if (msgDialog != null) {
                    msgDialog!!.dismiss()
                    msgDialog = null
                }
                msgDialog = MsgDialog()
                msgDialog!!.show(supportFragmentManager, "Msg")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeMsg() {
        try {
            if (msgDialog != null) {
                msgDialog!!.dismiss()
                msgDialog = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }

        if(intent.hasExtra("ReLogin")){
            finishAffinity()
            val intent = Intent(this, Logins::class.java)
            startActivity(intent)
            finishAndRemoveTask()						// 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid())	// 앱 프로세스 종료
        }
        else
        {
            finishAffinity()
            finishAndRemoveTask()						// 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid())	// 앱 프로세스 종료
        }
    }

    override fun showPassword(pw:String,txt:String) {
        try {
            runOnUiThread {
                if (password != null) {
                    password!!.dismiss()
                    password = null
                }
                password = ChangePasswordDialog(pw,txt)
                password!!.show(supportFragmentManager, "password")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closePassword() {
        try {
            if (password != null) {
                password!!.dismiss()
                password = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showDownLoading() {
        try {
            runOnUiThread {
                if (downloading != null) {
                    downloading!!.dismiss()
                    downloading = null
                }
                downloading = DownloadingDialog()
                downloading!!.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BBB)
                downloading!!.show(supportFragmentManager, "DownLoading")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeLoading() {
        try {
            if (loading != null) {
                loading!!.dismiss()
                loading = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeDownLoading() {
        try {
            if (downloading != null) {
                downloading!!.dismiss()
                downloading = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }
}