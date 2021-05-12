package com.beyondinc.commandcenter.service

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var am : ActivityManager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var tasks : MutableList<ActivityManager.RunningTaskInfo>? = am.getRunningTasks(Int.MAX_VALUE)
        if(tasks!!.isNotEmpty()){
            var tasksSize = tasks.size
            for(i in 0 until tasksSize){
                var taskinfo : ActivityManager.RunningTaskInfo = tasks[i]
                if(taskinfo.topActivity?.packageName.equals(context.applicationContext.packageName))
                {
                    am.moveTaskToFront(taskinfo.id,0)
                }
            }
        }
    }
}