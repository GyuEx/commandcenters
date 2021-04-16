package com.beyondinc.commandcenter.handler

import android.os.Handler
import android.os.Message
import android.util.Log

class MainThread(private val handler: Handler) : Thread() {
    override fun run() {
        val msg = Message()
        try {
            Thread.sleep(3000)
            Log.e("Thread","Text Out Live Thread")
            msg.what = 1
            handler.sendEmptyMessage(msg.what)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}