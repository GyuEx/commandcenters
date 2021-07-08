package com.beyondinc.commandcenter.activity

import RiderDialog
import StoreDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.databinding.ActivityMainBinding
import com.beyondinc.commandcenter.fragment.*
import com.beyondinc.commandcenter.service.AppActivateService
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel
import com.kakao.util.helper.Utility.getPackageInfo
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Mains : AppCompatActivity(), MainsFun {
    var binding: ActivityMainBinding? = null
    private val Tag = "Mains Activity"
    private val serviceIntent = Intent(AppActivateService.ACTION_FOREGROUND)

    companion object {
        var fr: Fragment? = null
        var oderfrag: Fragment? = null
        var mapfrag: Fragment? = null
        var checkfrag: Fragment? = null
        var infofrag : Fragment? = null
        var agency : Fragment? = null
        var rider : Fragment? = null
        var fragmentTransaction: FragmentTransaction? = null
        var fragmentTransactionSub: FragmentTransaction? = null
        var dialog:DialogFragment? = null
        var detail:DetailDialog? = null
        var history:HistoryDialog? = null
        var message:MessageDialog? = null
        var select:SelectDialog? = null
        var address:AddressDialog? = null
        var payment:PaymentDialog? = null
        var loading:LoadingDialog? = null

        val FINISH_INTERVAL_TIME: Long = 2000
        var backPressedTime: Long = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(Tag, "Destory")
//        exit()
//        (Vars.MainThread as ThreadFun).stopThread()
//        (Vars.AlarmThread as ThreadFun).stopThread()
//        (Vars.MarkerThread as ThreadFun).stopThread()
//        (Vars.HttpThread as ThreadFun).stopThread()
//
//        Vars.MainThread = null
//        Vars.AlarmThread = null
//        Vars.MarkerThread = null
//        Vars.HttpThread = null
    }

    override fun onStart() {
        super.onStart()
        Log.e(Tag, "Start")
    }

    override fun onResume() {
        super.onResume()
        Log.e(Tag, "Resume")
    }

    override fun onPause() {
        super.onPause()
        Log.e(Tag, "Pause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(Tag, "Stop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e(Tag, "Restart")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "세로모드", Toast.LENGTH_SHORT).show();
        }

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "가로모드", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.mContext = this
        Log.e(Tag, "Create")

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //프래그먼트를 재활용할것, 죽이게되면 뷰모델이 초기화됨

        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransactionSub = supportFragmentManager.beginTransaction()

        checkfrag = CheckListFragment()
        mapfrag = MapFragment()
        oderfrag = OrderFragment()
        infofrag = InfoFragment()
        agency = AgencyListFragment()
        rider = RiderListFragment()

        fragmentTransactionSub!!.add(R.id.mL02, checkfrag!!)
        fragmentTransactionSub!!.show(checkfrag!!)
        fragmentTransactionSub!!.commitAllowingStateLoss()

        //뷰를 선로드 및 재활용을 통해서 빠른 화면전환 기대할 수 있음
        fragmentTransaction!!.add(R.id.mL01, mapfrag!!)
        fragmentTransaction!!.add(R.id.mL01, oderfrag!!)
        fragmentTransaction!!.add(R.id.mL01, infofrag!!)
        fragmentTransaction!!.add(R.id.mL01, agency!!)
        fragmentTransaction!!.add(R.id.mL01, rider!!)

        fragmentTransaction!!.hide(oderfrag!!)
        fragmentTransaction!!.hide(infofrag!!)
        fragmentTransaction!!.hide(agency!!)
        fragmentTransaction!!.hide(mapfrag!!)
        fragmentTransaction!!.hide(rider!!)

        fr = infofrag
        fragmentTransaction!!.show(fr!!).commitAllowingStateLoss()

        if(Vars.MainVm == null)
        {
            Vars.MainVm = MainsViewModel()
            showLoading() // 최초 로딩화면 보여주기
        }
        binding!!.lifecycleOwner = this
        binding!!.viewModel = Vars.MainVm

        getKeyHash(this) // 해시키 얻기
    }

    fun getKeyHash(context: Context) {
        val packageInfo: PackageInfo = getPackageInfo(this, PackageManager.GET_SIGNATURES)
        for (signature in packageInfo.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                //Log.e("MAIN", "HASHKEY :: *" + encodeToString(md.digest(), NO_WRAP) + "*")
            } catch (e: NoSuchAlgorithmException) {
            }
        }
    }

    override fun onBackPressed() {

        Logindata.SessionExpireMin = Vars.timecntExit // 터치 이벤트 없으면 일정시간 후 앱 종료

        if(Vars.MainVm?.layer!!.value == Finals.SELECT_MENU)
        {
            val tempTime = System.currentTimeMillis()
            val intervalTime: Long = tempTime - backPressedTime
            if (intervalTime in 0..FINISH_INTERVAL_TIME) {
                exit()
            } else {
                backPressedTime = tempTime
                Toast.makeText(applicationContext, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            //Vars.MainVm?.layer!!.value = Finals.SELECT_MENU
            Vars.MainVm?.click_clean()
            setFragment()
        }
    }

    override fun sendSMS(num: String)
    {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.putExtra("address", num)
            i.putExtra("sms_body", "")
            i.type = "vnd.android-dir/mms-sms"
            this.startActivity(i)
        }
        catch (e: java.lang.Exception) {
            Toast.makeText(this,"문자발송을 할 수 없습니다.",Toast.LENGTH_LONG).show()
        }
    }

    override fun exit(){
        Vars.MainVm!!.Logout()
        stopService(serviceIntent)
        moveTaskToBack(true)						// 태스크를 백그라운드로 이동
        finishAndRemoveTask()						// 액티비티 종료 + 태스크 리스트에서 지우기
        android.os.Process.killProcess(android.os.Process.myPid())	// 앱 프로세스 종료
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        Logindata.SessionExpireMin = Vars.timecntExit // 터치 이벤트 없으면 일정시간 후 앱 종료
    }

    override fun re_login(type : Int) {
        Vars.MainVm!!.Logout()
        stopService(serviceIntent)
        val intent = Intent(Vars.mContext,Logins::class.java)
        if(type == Finals.CHANGE_PASSWORD) intent.putExtra("ReLogin",Finals.CHANGE_PASSWORD)
        else if (type == Finals.EXPIRE) intent.putExtra("Expire",Finals.EXPIRE)
        startActivity(intent)
        finishAffinity()
    }

    override fun setFragment() {
        if(Vars.MainVm?.layer!!.value == Finals.SELECT_MAP)
        {
            val ft = supportFragmentManager.beginTransaction()
            ft!!.hide(fr!!)
            fr = mapfrag
            ft!!.show(fr!!).commitAllowingStateLoss()
        }
        else if(Vars.MainVm?.layer!!.value == Finals.SELECT_ORDER)
        {
            val ft = supportFragmentManager.beginTransaction()
            ft!!.hide(fr!!)
            fr = oderfrag
            ft!!.show(oderfrag!!).commitAllowingStateLoss()
        }
        else if(Vars.MainVm?.layer!!.value == Finals.SELECT_MENU)
        {
            val ft = supportFragmentManager.beginTransaction()
            ft!!.hide(fr!!)
            fr = infofrag
            ft!!.show(infofrag!!).commitAllowingStateLoss()
        }
        else if(Vars.MainVm?.layer!!.value == Finals.SELECT_AGENCY)
        {
            val ft = supportFragmentManager.beginTransaction()
            ft!!.hide(fr!!)
            fr = agency
            ft!!.show(fr!!).commitAllowingStateLoss()
        }
        else if(Vars.MainVm?.layer!!.value == Finals.SELECT_RIDERLIST)
        {
            val ft = supportFragmentManager.beginTransaction()
            ft!!.hide(fr!!)
            fr = rider
            ft!!.show(fr!!).commitAllowingStateLoss()
        }
    }

    override fun closeDialog() {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeOderdetail() {
        try {
            if (detail != null) {
                detail!!.dismiss()
                detail = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeHistory() {
        try {
            if (history != null) {
                history!!.dismiss()
                history = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeMessage()
    {
        try {
            if (message != null) {
                message!!.dismiss()
                message = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeSelect()
    {
        try {
            if (select != null) {
                select!!.dismiss()
                select = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }


    override fun showDialogBrief() {
        try {
            runOnUiThread {
                if (dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
                dialog = BriefDialog()
                dialog!!.show(supportFragmentManager, "dialog")
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showDialogStore() {
        try {
            runOnUiThread {
                if (dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
                dialog = StoreDialog()
                dialog!!.show(supportFragmentManager, "dialog")
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showDialogRider() {
        try{
            runOnUiThread {
                if (dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
                dialog = RiderDialog()
                dialog!!.show(supportFragmentManager, "dialog")
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showOrderdetail(code:Int) {
        try {
            runOnUiThread {
                if (detail != null) {
                    detail!!.dismiss()
                    detail = null
                }
                detail = DetailDialog(code)
                detail!!.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light)
                detail!!.show(supportFragmentManager, "Details")
            }
        }catch (e: Exception) {
        //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showLoading() {
        try {
            runOnUiThread {
                if (loading != null) {
                    loading!!.dismiss()
                    loading = null
                }
                loading = LoadingDialog()
                loading!!.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BBB)
                loading!!.show(supportFragmentManager, "Loading")
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

    override fun showHistory() {
        try {
            runOnUiThread {
                if (history != null) {
                    history!!.dismiss()
                    history = null
                }
                history = HistoryDialog()
                history!!.show(supportFragmentManager, "History")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showMessage(msg: String, num: String) {
        try {
            runOnUiThread {
                if (message != null) {
                    message!!.dismiss()
                    message = null
                }
                message = MessageDialog(num)
                message!!.show(supportFragmentManager, msg)
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }

    }

    override fun showSelect() {
        try {
            runOnUiThread {
                if (select != null) {
                    select!!.dismiss()
                    select = null
                }
                select = SelectDialog()
                select!!.show(supportFragmentManager, "Select")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }

    }

    override fun showAddress(obj : Any?) {
        try {
            runOnUiThread {
                if (address != null) {
                    address!!.dismiss()
                    address = null
                }
                address = AddressDialog(obj)
                address!!.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light)
                address!!.show(supportFragmentManager, "address")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showPayment() {
        try {
            runOnUiThread {
                if (payment != null) {
                    payment!!.dismiss()
                    payment = null
                }
                payment = PaymentDialog()
                payment!!.show(supportFragmentManager, "Select")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun changeClose()
    {
        try {
            if (address != null) {
                address!!.dismiss()
                address = null
            }
            if (payment != null) {
                payment!!.dismiss()
                payment = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun setting(){
        startActivity(Intent(Vars.mContext, Setting::class.java))
    }

    override fun dispatchTouchEvent() {
        val focusView: View? = currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(focusView?.windowToken, 0)
    }

    override fun detail_Fragment(i: Int){
        detail!!.setFragment(i)
    }

    override fun send_call(tel: String) {
        var intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:${tel}")
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }
    }
}