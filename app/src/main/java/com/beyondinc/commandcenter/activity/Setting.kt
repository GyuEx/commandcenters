package com.beyondinc.commandcenter.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.SettingFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.ActivitySettingBinding
import com.beyondinc.commandcenter.databinding.EmergencyDialogBinding
import com.beyondinc.commandcenter.fragment.*
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.SettingViewModel

class Setting : AppCompatActivity(), SettingFun {

    var binding: ActivitySettingBinding? = null
    var viewModel: SettingViewModel? = null
    var dialog : DialogFragment? = null
    var emer : EmerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Log.e("Setting", "On Create init")
         Vars.sContext = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
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
        try {
            if (emer != null) {
                emer!!.dismiss()
                emer = null
            }
        } catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showDialog() {
        try {
            runOnUiThread {
                if (dialog != null) {
                    dialog!!.dismiss()
                    dialog = null
                }
                dialog = NickDialog()
                dialog!!.show(supportFragmentManager, "Nick")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun showEmer() {
        try {
            runOnUiThread {
                if (emer != null) {
                    emer!!.dismiss()
                    emer = null
                }
                emer = EmerDialog()
                emer!!.show(supportFragmentManager, "Emer")
            }
        }catch (e: Exception) {
            //Log.e("MAIN", Log.getStackTraceString(e))
        }
    }

    override fun Exit(){

        Log.e("aaaaaaaa","aaaaaaaaaaaaaaaaaaaaaaaaaaa")

        var intent : Intent = Intent()
        //intent.putExtra("AAA","aaaaa")
        //setResult(RESULT_OK,intent)
        setResult(RESULT_OK)
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.e("Setting", "Destory")
    }
}