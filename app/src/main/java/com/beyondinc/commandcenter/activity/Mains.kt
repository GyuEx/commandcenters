package com.beyondinc.commandcenter.activity

import RiderDialog
import StoreDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.ActivityMainBinding
import com.beyondinc.commandcenter.fragment.*
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel


class Mains : AppCompatActivity(), MainsFun {
    var binding: ActivityMainBinding? = null
    var viewModel: MainsViewModel? = null
    private val Tag = "Mains Activity"

    companion object {
        var fr: Fragment? = null
        var oderfrag: Fragment? = null
        var mapfrag: Fragment? = null
        var checkfrag: Fragment? = null
        var fragmentTransaction: FragmentTransaction? = null
        var fragmentTransactionSub: FragmentTransaction? = null
        var dialog:DialogFragment? = null
        var detail:DetailDialog? = null
        var history:HistoryDialog? = null

        val FINISH_INTERVAL_TIME: Long = 2000
        var backPressedTime: Long = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(Tag, "Destory")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.mContext = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //프래그먼트를 재활용할것, 죽이게되면 뷰모델이 초기화됨

        fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransactionSub = supportFragmentManager.beginTransaction()

        checkfrag = CheckListFragment()
        mapfrag = MapFragment()
        oderfrag = OrderFragment()

        fragmentTransactionSub!!.add(R.id.mL02, checkfrag!!)
        fragmentTransactionSub!!.show(checkfrag!!)
        fragmentTransactionSub!!.commit()


        //뷰를 선로드 및 재활용을 통해서 빠른 화면전환 기대할 수 있음
        fragmentTransaction!!.add(R.id.mL01, oderfrag!!)
        fragmentTransaction!!.add(R.id.mL01, mapfrag!!)
        fragmentTransaction!!.hide(oderfrag!!)
        fragmentTransaction!!.show(mapfrag!!)
        fragmentTransaction!!.commit()

        viewModel = ViewModelProvider(this).get(MainsViewModel::class.java)
        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel

        getDeviceSize()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {

        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - backPressedTime
        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            moveTaskToBack(true);						// 태스크를 백그라운드로 이동
            finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setFragment() {
        if(viewModel!!.layer.value == Finals.SELECT_MAP)
        {
            val ft = supportFragmentManager.beginTransaction()
            ft!!.hide(oderfrag!!)
            ft!!.show(mapfrag!!).commit()
        }
        else if(viewModel!!.layer.value == Finals.SELECT_ORDER)
        {
            val ft = supportFragmentManager.beginTransaction()
            ft!!.hide(mapfrag!!)
            ft!!.show(oderfrag!!).commit()
        }
    }

    fun getDeviceSize() {
        /// 사용하는 핸드폰 전체 화면 사이즈 가져옴
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        display.getSize(Vars.DeviceSize)
    }



    override fun closeDialog() {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }
        } catch (e: Exception) {
            Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeOderdetail() {
        try {
            if (detail != null) {
                detail!!.dismiss()
                detail = null
            }
        } catch (e: Exception) {
            Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun closeHistory() {
        try {
            if (history != null) {
                history!!.dismiss()
                history = null
            }
        } catch (e: Exception) {
            Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

   override fun showDialogBrief() {
        runOnUiThread {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }
            dialog = BriefDialog()
            dialog!!.show(supportFragmentManager, "dialog")
        }
    }

    override fun showDialogStore() {
        runOnUiThread {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }
            dialog = StoreDialog()
            dialog!!.show(supportFragmentManager, "dialog")
        }
    }

    override fun showDialogRider() {
        runOnUiThread {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }
            dialog = RiderDialog()
            dialog!!.show(supportFragmentManager, "dialog")
        }
    }

    override fun showOderdetail() {
        runOnUiThread {
            if (detail != null) {
                detail!!.dismiss()
                detail = null
            }
            detail = DetailDialog()
            detail!!.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light)
            detail!!.show(supportFragmentManager, "Details")
        }
    }

    override fun showHistory() {
        runOnUiThread {
            if (history != null) {
                history!!.dismiss()
                history = null
            }
            history = HistoryDialog()
            history!!.show(supportFragmentManager, "History")
        }
    }

    override fun dispatchTouchEvent() {
        val focusView: View? = currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(focusView?.windowToken, 0)
    }
}