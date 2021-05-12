package com.beyondinc.commandcenter.handler

import android.media.AudioManager
import android.media.SoundPool
import android.speech.tts.TextToSpeech
import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.util.AlarmCodes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import java.util.*

class PlaySoundThread(private val oid:Int, private val sid:Int) : Thread(), ThreadFun {
    private var soundPool: SoundPool? = null
    private var isKeep = false

    override fun run()
    {
        if(Vars.UseTTS)
        {
            if(Vars.tts == null) Vars.tts = TextToSpeech(Vars.mContext){Vars.tts?.language = Locale.KOREAN}
            isKeep = true
            var Agencyname : String = ""
            var Ridername : String = ""

            while (isKeep)
            {
                Thread.sleep(1000) // 첫딜레이 및 루프진행
                if(Vars.orderList.containsKey(oid.toString()))
                {
                    Agencyname = Vars.orderList[oid.toString()]!!.AgencyName
                    Ridername = Vars.orderList[oid.toString()]!!.RiderName
                    isKeep = false // 탈출
                    break
                }
            }

            Log.e("1", "5 // ${Vars.UseJ} // ${Vars.UseTTS}")
            when (sid) {
                AlarmCodes.ALARM_ORDER_STATE_301 -> if(Vars.UseJ) ttsSpeak("$Agencyname 오더 접수")
                AlarmCodes.ALARM_ORDER_STATE_303 -> if(Vars.UseB) ttsSpeak("$Ridername 라이더 $Agencyname 오더 배정")
               //AlarmCodes.ALARM_ORDER_STATE_305 -> if(Vars.UseJ) ttsSpeak("$name 오더 접수") // 픽업은 없엉
                AlarmCodes.ALARM_ORDER_STATE_308 -> if(Vars.UseW) ttsSpeak("$Agencyname 오더 완료")
                AlarmCodes.ALARM_ORDER_STATE_401 -> if(Vars.UseC) ttsSpeak("$Agencyname 오더 취소")
            }

        }
        else
        {
            soundPool = SoundPool(1, AudioManager.STREAM_RING, 0)
            var wav: Int? = null
            when (sid) {
                AlarmCodes.ALARM_ORDER_STATE_301 -> if(Vars.UseJ) wav = Finals.soundArray[0]
                AlarmCodes.ALARM_ORDER_STATE_303 -> if(Vars.UseB) wav = Finals.soundArray[1]
                //AlarmCodes.ALARM_ORDER_STATE_305 -> if(Vars.UseJ) wav = Finals.soundArray[] // 픽업은 없엉
                AlarmCodes.ALARM_ORDER_STATE_308 -> if(Vars.UseW) wav = Finals.soundArray[2]
                AlarmCodes.ALARM_ORDER_STATE_401 -> if(Vars.UseC) wav = Finals.soundArray[3]
            }

            if (wav != null) {
                var SoundID = soundPool!!.load(Vars.mContext, wav, 0)
                soundPool!!.setOnLoadCompleteListener { soundPool, sampleId, status ->
                    if (SoundID != null) {
                        soundPool.play(SoundID, 1f, 1f, 0, 0, 1.0f)
                    }
                }
                soundPool!!.unload(wav)
            }
        }
    }

    fun ttsSpeak(strTTS : String){
        Vars.tts?.speak(strTTS, TextToSpeech.QUEUE_ADD, null, null)
    }

    override fun stopThread() {
        TODO("Not yet implemented")
    }
}