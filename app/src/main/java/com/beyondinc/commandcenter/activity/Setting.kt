package com.beyondinc.commandcenter.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.SettingFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.ActivitySettingBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.SettingViewModel

class Setting : AppCompatActivity(), SettingFun {

    var binding: ActivitySettingBinding? = null
    var viewModel: SettingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("Setting", "On Create init")
         Vars.sContext = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        viewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    override fun Exit(){
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Setting", "Destory")
    }
}