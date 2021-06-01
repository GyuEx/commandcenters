package com.beyondinc.commandcenter.net

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.util.Crypto
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HttpConn : Thread(), ThreadFun {

    var isKeep : Boolean = false
    var code : String? = null
    var context : Context? = null
    var dofalseConn = 0

    init {
        isKeep = true
    }

    override fun stopThread() {
        isKeep = false
    }

    fun makeRequestJSON(responseCipherKey: String,code:String,message:JSONArray): JSONObject {
        val requestJson = JSONObject()
        requestJson["Id"] = "0"
        requestJson["Cipherkey"] = responseCipherKey

        val methodJsonArray = JSONArray()
        for (item in message) {
            val methodJson = JSONObject()
            methodJson["Name"] = code
            methodJson["data"] = item
            methodJsonArray.add(methodJson)
        }
        requestJson["Method"] = methodJsonArray
        return requestJson
    }

    fun makeResponseData(responseData: JSONArray): ArrayList<ConcurrentHashMap<String, String>> {
        val returnData: ArrayList<ConcurrentHashMap<String, String>> = ArrayList()

        for (block in responseData.indices) {
            val joMethod = responseData[block] as JSONObject
            val jaData = joMethod["data"] as JSONArray?
            if (null != jaData) {
                for (j in jaData.indices) {
                    val joData = jaData[j] as JSONObject
                    val map = ConcurrentHashMap<String, String>()
                    val keySet = joData.keys
                    for (sName in keySet) {
                        map[(sName as String)] = joData[sName].toString()
                    }
                    returnData.add(map)
                }
            }
        }
        return returnData
    }

    override fun run() {
        while (isKeep)
        {
            try
            {
                if (Vars.sendList != null && Vars.sendList.isNotEmpty())
                {
                    val jsonMessage = Vars.sendList.removeAt(0)

                    code = jsonMessage.keys.iterator().next()
                    val data = jsonMessage[code]

//                    Log.e("Connect", "" + code)
//                    Log.e("Connect", "" + data)

                    val requestCipherKey = Crypto.generateMD5Hash(Vars.lContext!!.resources.getString(R.string.default_token))!!.toLowerCase(Locale.getDefault())
                    val responseCipherKey = Crypto.generateMD5Hash(Crypto.getCurrentTimeKey())!!.toLowerCase(Locale.getDefault())
                    val requestJson = makeRequestJSON(responseCipherKey, code!!,data!!)
                    val requestPlainString = requestJson.toString()
                    val requestBody = Crypto.AES256.encrypt(requestPlainString, requestCipherKey)
                    var receiveDoc = StringBuilder()

                    val url = URL(Vars.lContext!!.resources.getString(R.string.remote_connect_url) + Vars.lContext!!.resources.getString(R.string.remote_endpoint_url))
                    val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                    con.connectTimeout = 3000 //서버에 연결되는 Timeout 시간 설정
                    con.readTimeout = 3000 // InputStream 읽어 오는 Timeout 시간 설정
                    con.setRequestProperty("UserAddData", "2")
                    con.setRequestProperty("content-type", "application/json; charset=utf-8")
                    con.setRequestProperty("authorization", Vars.lContext!!.resources.getString(R.string.default_token))
                    con.requestMethod = "POST"

                    //json으로 message를 전달하고자 할 때
                    con.doInput = true
                    con.doOutput = true //POST 데이터를 OutputStream으로 넘겨 주겠다는 설정
                    con.useCaches = false
                    con.defaultUseCaches = false
                    val wr = OutputStreamWriter(con.outputStream)
                    wr.write(requestBody) //json 형식의 message 전달
                    wr.flush()
                    val sb = StringBuilder()
                    if (con.getResponseCode() === HttpURLConnection.HTTP_OK)
                    {
                        //Stream을 처리해줘야 하는 귀찮음이 있음.
                        val br = BufferedReader(
                                InputStreamReader(con.inputStream, "utf-8")
                        )
                        var line: String?
                        while (br.readLine().also { line = it } != null) {
                            sb.append(line).append("\n")
                        }
                        br.close()

                        receiveDoc = sb

                        val receivedEncryptedBody = receiveDoc.toString()
                        val receivedBody = Crypto.AES256.decrypt(receivedEncryptedBody, responseCipherKey)
                        val parser = JSONParser()
                        val results: JSONObject = parser.parse(receivedBody) as JSONObject
                        val resultMethodBlock: JSONArray = results["Method"] as JSONArray
                        val returnData = makeResponseData(resultMethodBlock)

                        //Log.e("HTTP" , "" + resultMethodBlock)

                        var temp : ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String, String>>> = ConcurrentHashMap()
                        temp[code!!] = returnData
                        Vars.receiveList.add(temp)

                        if(dofalseConn > 5) // 여기까지 왔다면 통신이 되었다는 이야기므로 통신불능안내창을 종료하고 카운터를 0으로 변경 Exceptin부분에 통신이상 구현
                        {
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_LOADING,0).sendToTarget()
                            dofalseConn = 0
                        }

                    } else {
                        Log.e("HTTP", con.responseMessage)
                    }
                    Thread.sleep(100)
                }

            } catch (e: Exception) {
                if (code == Procedures.LOGIN) Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.LOGIN_FAIL,0).sendToTarget()
                else
                {
                    if(dofalseConn == 5) // 통신불능시 해당 Exception을 타게됨, 카운터를 줘서 5회 이상이면 통신장애안내창 띄움
                    {
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.HTTP_ERROR,0).sendToTarget()
                        dofalseConn++ // 창을 두세번띄울 필요는 없으므로 카운터는 계속 진행-- 상단에서 초기화
                    }
                    else
                    {
                        dofalseConn++
                    }
                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.DISCONN_ALRAM,0).sendToTarget() // 데이터전송실패시 알람을 꺼버림
                    Vars.timecntOT = 10 // 갯수조회 카운터를 한시적으로 10초로 변경
                }
            }
        }
    }
}