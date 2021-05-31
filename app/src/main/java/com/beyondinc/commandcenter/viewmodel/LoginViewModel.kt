package com.beyondinc.commandcenter.viewmodel
import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.BuildConfig
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class LoginViewModel() : ViewModel() {

    var id : MutableLiveData<String> = MutableLiveData()
    var pw : MutableLiveData<String> = MutableLiveData()

    var saveId : MutableLiveData<Boolean> = MutableLiveData()
    var savePw : MutableLiveData<Boolean> = MutableLiveData()

    var updateTxt = MutableLiveData<String>()
    var infotxt = MutableLiveData<String>()

    var ver = MutableLiveData<String>()
    var proVal = MutableLiveData<Int>()

    private var downloadID: Long = -1
    private lateinit var file: File
    private lateinit var delpath: File
    private var downloadManager: DownloadManager? = null

    init {
        Logindata.isLogin = false

        Log.e("오오오","오오오오오오오")

        saveId.value = false
        savePw.value = false

        ver.value = Logindata.appVersion

        proVal.value = 0

        val pref = PreferenceManager.getDefaultSharedPreferences(Vars.lContext)
        if(pref.getBoolean("saveid", false))
        {
            saveId.value = true
            id.value = pref.getString("id", "")
        }
        if(pref.getBoolean("savepw", false))
        {
            savePw.value = true
            pw.value = pref.getString("pw", "")
        }

        //getPhoneNum()

    }

    override fun onCleared() {
        super.onCleared()
        Vars.LoginVm = null
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
            (Vars.lContext as LoginsFun).showLoading()

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

    fun LoginSucess(){
        (Vars.lContext as LoginsFun).LoginSuccess()
    }

    fun LoginFail(){
        if(Logindata.MSG == null)
        {
            var toast : Toast = Toast.makeText(
                Vars.lContext,
                "서버접속실패, 다시 시도해주세요.",
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP, 0, 300)
            toast.show()
        }
        else
        {
            var toast : Toast = Toast.makeText(
                Vars.lContext,
                Logindata.MSG,
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP, 0, 300)
            toast.show()
        }
        (Vars.lContext as LoginsFun).LoginFail()
    }

    fun getPhoneNum(){
        var tel : TelephonyManager = Vars.lContext!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(Vars.lContext!!, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                Vars.lContext!!,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                Vars.lContext!!,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        Logindata.devicePhone = tel.line1Number.toString()
        Logindata.deviceModel = Build.MODEL
    }

    fun downloadApk() {

        (Vars.lContext as LoginsFun).closeLoading()
        (Vars.lContext as LoginsFun).showDownLoading()

        updateTxt.value = "업데이트가 있어요! \n 꼭 설치버튼을 눌러주세요."
        infotxt.value = "- 안 내 - \n 업데이트 도중에 WIFI 또는 무선데이터 연결을 해제하지 마십시오.\n 본 화면이 오랫동안 지속되면 앱을 강제로 종료 후 다시 실행하여 주십시오."

        val intentFilter = IntentFilter()
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        Vars.lContext!!.registerReceiver(completeReceiver, intentFilter)

        var filearry = arrayOf(Vars.lContext!!.getExternalFilesDir(null))
        for(d in filearry)
        {
            d!!.delete()
        }
        //일단 쓸대없는 이전버전등의 파일은 전체 삭제 진행

        downloadManager = Vars.lContext!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val fileName = "${BuildConfig.FLAVOR}_manager_${Logindata.MSG}.apk"
        file = File(Vars.lContext!!.getExternalFilesDir(null), fileName)

        val fileUri = Uri.fromFile(file)
        val downloadUrl = "${Vars.lContext!!.getString(R.string.app_down_url)}${Logindata.appID}"

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("새로운 버전을 받고 있습니다")
            .setDescription("영차..영차..")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(fileUri)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadID = downloadManager!!.enqueue(request)

        Thread {
            var downloading = true
            while (downloading) {
                val q = DownloadManager.Query()
                q.setFilterById(downloadID)
                val cursor = downloadManager!!.query(q)
                cursor.moveToFirst()
                val bytes_downloaded: Int = cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                )
                val bytes_total: Int =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) === DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val dl_progress = (bytes_downloaded * 100) / bytes_total
                proVal.postValue(dl_progress)
                cursor.close()
            }
        }.start()
    }

    fun installApk() {
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val uri = FileProvider.getUriForFile(
                Vars.lContext!!,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                file
            )
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
                            (Vars.lContext as LoginsFun).closeDownLoading()
                        }
                    }
                }
            }
        }
    }
}