package com.beyondinc.commandcenter.handler

import android.os.Handler
import android.os.Message
import android.util.Log
import com.beyondinc.commandcenter.data.Orderdata
import com.beyondinc.commandcenter.util.Vars
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.EmptyCoroutineContext.plus

class MainThread(private val handler: Handler) : Thread() {

    var isKeep : Boolean = false

    init {
        isKeep = true
    }


    override fun run() {
        var list : HashMap<Int,Orderdata> ?= null
        try {

            while (isKeep) {
                Thread.sleep(10000)

                val random = Random()
                val num = random.nextInt(10)

                if (list == null) {
                    list = HashMap()
                }

                for (i in num..10) {
                    var state : String
                    if((num + i)%2 == 1) state = "픽업"
                    else state = "완료"
                    val memo = Orderdata()
                    memo.id = i
                    memo.usetime = i.toString() + "분"
                    memo.resttime = "$i 초"
                    memo.pay = "카드"
                    memo.title = "면곡당 " + i + "호점"
                    memo.adress = "장안동 " + i + "번지"
                    memo.poi = i.toString() + "편한세상"
                    memo.rider = "$i km"
                    memo.work = state
                    memo.paywon = "$i 원"
                    memo.delay = i
                    list!![i-(num)] = memo
                }

                Vars.oderList?.clear()
                list!!.let { Vars.oderList?.putAll(it) }
                list.clear()

                handler.obtainMessage(1).sendToTarget()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Thread" , e.toString())
        }
    }
}