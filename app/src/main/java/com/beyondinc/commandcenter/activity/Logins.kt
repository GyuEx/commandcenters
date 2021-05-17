package com.beyondinc.commandcenter.activity

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.BuildConfig
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.databinding.ActivityLoginsBinding
import com.beyondinc.commandcenter.fragment.LoadingDialog
import com.beyondinc.commandcenter.fragment.LoginLoadingDialog
import com.beyondinc.commandcenter.handler.AlarmThread
import com.beyondinc.commandcenter.handler.CheckThread
import com.beyondinc.commandcenter.handler.MainThread
import com.beyondinc.commandcenter.handler.MarkerThread
import com.beyondinc.commandcenter.net.HttpConn
import com.beyondinc.commandcenter.service.ForecdTerminationService
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.MakeJsonParam
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.CheckViewModel
import com.beyondinc.commandcenter.viewmodel.LoginViewModel
import org.json.simple.JSONArray
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class Logins : AppCompatActivity() , LoginsFun {
    var binding: ActivityLoginsBinding? = null
    var viewModel: LoginViewModel? = null
    var isLogin = false
    var loading:LoginLoadingDialog? = null

    private var downloadID: Long = -1
    private lateinit var file: File
    private var downloadManager: DownloadManager? = null

    private val Tag = "Logins Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.lContext = this

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        checkpermission()

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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_logins)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        //Log.e("OnCreate", "OnStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.e(Tag, "Destory")
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
        runOnUiThread {
            if (loading != null) {
                loading!!.dismiss()
                loading = null
            }
            loading = LoginLoadingDialog()
            loading!!.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BBB)
            loading!!.show(supportFragmentManager, "Loading")
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

    override fun downloadApk() {

        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        this.registerReceiver(completeReceiver, intentFilter)

        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val fileName = "${BuildConfig.FLAVOR}_manager_${Logindata.MSG}.apk"
        file = File(this.getExternalFilesDir(null), fileName)
        val fileUri = Uri.fromFile(file)
        val downloadUrl = "${getString(R.string.app_down_url)}${Logindata.appID}"

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("새로운 버전을 받고 있습니다")
            .setDescription("영차..영차..")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(fileUri)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadID = downloadManager!!.enqueue(request)


    }

    override fun installApk() {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileprovider", file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            Vars.lContext!!.startActivity(intent)
        } else {
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            Vars.lContext!!.startActivity(intent)
        }
    }

    private val completeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    if (id == downloadID) {
                        val query: DownloadManager.Query = DownloadManager.Query()
                        query.setFilterById(id)
                        val cursor = downloadManager!!.query(query)
                        if (!cursor.moveToFirst()) {
                            return
                        }
                        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val status = cursor.getInt(columnIndex)
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            installApk()
                        }
                    }
                }
            }
        }
    }
}