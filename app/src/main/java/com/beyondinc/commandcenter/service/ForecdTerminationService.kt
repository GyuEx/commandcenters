package com.beyondinc.commandcenter.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable

class ForecdTerminationService : Service() {

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    // 앱이 불특정하게 종료시 Destory부분을 안내받기 위해 구현, 실제로 사용하지는 않음

    override fun onTaskRemoved(rootIntent: Intent) { //핸들링 하는 부분
        Log.e("Error", "onTaskRemoved - 강제 종료 $rootIntent")
        stopSelf() //서비스 종료
    }
}