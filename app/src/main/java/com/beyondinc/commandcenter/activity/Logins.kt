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

        checkpermission() // 퍼미션 가져오기

        if(Vars.MainThread == null)
        {
            Vars.MainThread = MainThread()
            Vars.MainThread!!.isDaemon = true
            Vars.MainThread!!.start()
        } //외부데이터 처리해줄 메인 쓰레드
        if(Vars.HttpThread == null)
        {
            Vars.HttpThread = HttpConn()
            Vars.HttpThread!!.isDaemon = true
            Vars.HttpThread!!.start()
        } //서버랑 통신할 통신 쓰레드
        if(Vars.AlarmThread == null)
        {
            Vars.AlarmThread = AlarmThread()
            Vars.AlarmThread!!.isDaemon = true
            Vars.AlarmThread!!.start()
        } //서버 알람 받아 처리하는 알람 쓰레드
        if(Vars.MarkerThread == null)
        {
            Vars.MarkerThread = MarkerThread()
            Vars.MarkerThread!!.isDaemon = true
            Vars.MarkerThread!!.start()
        } //맵뷰가 마커를 직접생성하면 느려서 마커를 관리해주는 마커 쓰레드
        if(Vars.CheckThread == null)
        {
            Vars.CheckThread = CheckThread()
            Vars.CheckThread!!.isDaemon = true
            Vars.CheckThread!!.start()
        } // 주기적으로 서버에 요청할 쓰레드 (뷰모델에 타이머를 줬더니 힘들어하는것 같아서 뺌)

        if(Vars.DataHandler == null) Vars.DataHandler = Handler(HandlerCallBack()) // 모델 데이터 처리 핸들러

        binding = DataBindingUtil.setContentView(this, R.layout.activity_logins_new)
        if(Vars.LoginVm == null) Vars.LoginVm = LoginViewModel()
        binding!!.lifecycleOwner = this
        binding!!.viewModel = Vars.LoginVm

        getDeviceSize() // 단말기 화면 사이즈 얻기

        if(intent.hasExtra("ReLogin")){
            showPassword(Logindata.LoginPw.toString(),"변경할 비밀번호를 입력해주세요")
        }
        else if(intent.hasExtra("Expire")){
            Vars.LoginVm?.ExpireMessage!!.value = "장시간 사용하지 않아\n시스템을 종료 합니다"
            showMsg()
        }
    }

    fun getDeviceSize() {
        /// 사용하는 핸드폰 전체 화면 사이즈 가져옴
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        display.getSize(Vars.DeviceSize)
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
        startActivity(Intent(Vars.lContext, Mains::class.java))
        finish()
    }

    override fun LoginFail() {
        closeLoading()
        isLogin = false
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
            exitProcess(0)
        }
        else
        {
            finishAffinity()
            exitProcess(0)
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
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED

            ) pms.add(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES
                ) !== PackageManager.PERMISSION_GRANTED

            ) pms.add(
                Manifest.permission.REQUEST_INSTALL_PACKAGES
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.RECORD_AUDIO
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) !== PackageManager.PERMISSION_GRANTED
            ) pms.add(
                Manifest.permission.READ_PHONE_STATE
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) !== PackageManager.PERMISSION_GRANTED

            ) pms.add(
                Manifest.permission.READ_PHONE_NUMBERS
            )
            if (pms.size == 0) {

            } else ActivityCompat.requestPermissions(
                this,
                pms.toTypedArray(), Finals.REQUEST_PERMISSION
            )
        }
    }
}