package com.beyondinc.commandcenter.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.ActivityLoginsBinding
import com.beyondinc.commandcenter.util.Vars
import com.beyondinc.commandcenter.viewmodel.LoginsViewModel

class Logins : AppCompatActivity() , LoginsFun {
    var binding: ActivityLoginsBinding? = null
    var viewModel: LoginsViewModel? = null

    private val Tag = "Logins Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Vars.mContext = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logins)
        viewModel = ViewModelProvider(this).get(LoginsViewModel::class.java)
        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(Tag, "Destory")
    }

    override fun LoginSuccess() {
        startActivity(Intent(applicationContext, Mains::class.java))
        finish()
    }

}