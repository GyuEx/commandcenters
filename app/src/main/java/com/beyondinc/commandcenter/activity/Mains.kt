package com.beyondinc.commandcenter.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.ActivityMainBinding
import com.beyondinc.commandcenter.fragment.*
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.MainsViewModel


class Mains : AppCompatActivity(), MainsFun {
    var binding: ActivityMainBinding? = null
    //var viewModel: MainsViewModel? = null
    private val Tag = "Mains Activity"

    companion object {
        var fr: Fragment? = null
        var oderfrag: Fragment? = null
        var mapfrag: Fragment? = null
        var checkfrag: Fragment? = null
        var fragmentTransaction: FragmentTransaction? = null
        var dialog:SelectDialog? = null
        var detail:DetailDialog? = null
        var history:HistoryDialog? = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(Tag, "Destory")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.mContext = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Vars.mvm = ViewModelProvider(this).get(MainsViewModel::class.java)
        binding!!.lifecycleOwner = this
        binding!!.viewModel = Vars.mvm

        Log.e(Tag, "On Create // " + Vars.isLogin)

        setFragment()

        fragmentTransaction = supportFragmentManager.beginTransaction()
        checkfrag = CheckListFragment()
        fr = checkfrag
        fragmentTransaction!!.replace(R.id.mL02, fr!!)
        fragmentTransaction!!.show(fr!!)
        fragmentTransaction!!.commitAllowingStateLoss()

        getDeviceSize()
    }

    override fun setFragment() {
        if(Vars.mvm!!.layer.value == Vars.mvm!!.SELECT_MAP)
        {
            fragmentTransaction = supportFragmentManager.beginTransaction()
            mapfrag = DriverFragment()
            fr = mapfrag
            fragmentTransaction!!.replace(R.id.mL01, fr!!)
            fragmentTransaction!!.show(fr!!)
            fragmentTransaction!!.commitAllowingStateLoss()
        }
        else if(Vars.mvm!!.layer.value == Vars.mvm!!.SELECT_ORDER)
        {
            fragmentTransaction = supportFragmentManager.beginTransaction()
            oderfrag = OrderFragment()
            fr = oderfrag
            fragmentTransaction!!.replace(R.id.mL01, fr!!)
            fragmentTransaction!!.show(fr!!)
            fragmentTransaction!!.commitAllowingStateLoss()
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

    override fun showDialog() {
        runOnUiThread {
            if (dialog != null) {
                dialog!!.dismiss()
                dialog = null
            }
            dialog = SelectDialog()
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

    override fun showDrawer(){
        binding!!.Mains!!.openDrawer(binding!!.mdL01)
    }

    override fun closeDrawer() {
        binding!!.Mains!!.closeDrawer(binding!!.mdL01)
    }
}