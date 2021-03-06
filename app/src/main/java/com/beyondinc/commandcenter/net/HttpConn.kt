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
import java.net.SocketTimeoutException
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
                if (Vars.sendList.size > 0 && Vars.sendList.isNotEmpty() && !Vars.sendList.isNullOrEmpty())
                {
                    val jsonMessage = Vars.sendList.removeAt(0)

                    code = jsonMessage.keys.iterator().next()
                    val data = jsonMessage[code]

//                    Log.e("Connect", "" + code)
                    Log.e("Connect", "" + data)

                    val requestCipherKey = Crypto.generateMD5Hash(Vars.lContext!!.resources.getString(R.string.default_token))!!.toLowerCase(Locale.getDefault())
                    val responseCipherKey = Crypto.generateMD5Hash(Crypto.getCurrentTimeKey())!!.toLowerCase(Locale.getDefault())
                    val requestJson = makeRequestJSON(responseCipherKey, code!!,data!!)
                    val requestPlainString = requestJson.toString()
                    val requestBody = Crypto.AES256.encrypt(requestPlainString, requestCipherKey)
                    var receiveDoc = StringBuilder()

                    val url = URL(Vars.lContext!!.resources.getString(R.string.remote_connect_url) + Vars.lContext!!.resources.getString(R.string.remote_endpoint_url))
                    val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                    con.connectTimeout = 3000 //????????? ???????????? Timeout ?????? ??????
                    con.readTimeout = 3000 // InputStream ?????? ?????? Timeout ?????? ??????
                    con.setRequestProperty("UserAddData", "2")
                    con.setRequestProperty("content-type", "application/json; charset=utf-8")
                    con.setRequestProperty("authorization", Vars.lContext!!.resources.getString(R.string.default_token))
                    con.requestMethod = "POST"

                    //json?????? message??? ??????????????? ??? ???
                    con.doInput = true
                    con.doOutput = true //POST ???????????? OutputStream?????? ?????? ???????????? ??????
                    con.useCaches = false
                    con.defaultUseCaches = false
                    val wr = OutputStreamWriter(con.outputStream)
                    wr.write(requestBody) //json ????????? message ??????
                    wr.flush()
                    val sb = StringBuilder()
                    val HttpResultCode = HttpURLConnection.HTTP_OK
                    if (con.responseCode == HttpResultCode)
                    {
                        //Stream??? ??????????????? ?????? ???????????? ??????.
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

                        Log.e("HTTP" , "" + receivedBody)

                        if(!results.containsKey("Method")) return

                        val resultMethodBlock: JSONArray = results["Method"] as JSONArray
                        val returnData = makeResponseData(resultMethodBlock)

                        var temp : ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String, String>>> = ConcurrentHashMap()
                        temp[code!!] = returnData
                        Vars.receiveList.add(temp)

                        if(dofalseConn > 5) // ???????????? ????????? ????????? ???????????? ??????????????? ???????????????????????? ???????????? ???????????? 0?????? ?????? Exceptin????????? ???????????? ??????
                        {
                            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.CLOSE_LOADING,0).sendToTarget()
                            dofalseConn = 0
                        }

                    } else {
                        Log.e("HTTP", con.responseMessage)
                    }
                }
           } catch (e: SocketTimeoutException) {
                if (code == Procedures.LOGIN) Vars.DataHandler!!.obtainMessage(Finals.VIEW_LOGIN,Finals.LOGIN_FAIL,0).sendToTarget()
//                else
//                {
//                    if(dofalseConn == 5) // ??????????????? ?????? Exception??? ?????????, ???????????? ?????? 5??? ???????????? ????????????????????? ??????
//                    {
//                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.HTTP_ERROR,0).sendToTarget()
//                        dofalseConn++ // ?????? ??????????????? ????????? ???????????? ???????????? ?????? ??????-- ???????????? ?????????
//                    }
//                    else
//                    {
//                        dofalseConn++
//                    }
//                    Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.DISCONN_ALRAM,0).sendToTarget() // ???????????????????????? ????????? ?????????
//                    Vars.timecntOT = 10 // ???????????? ???????????? ??????????????? 10?????? ??????
//                }
            }
            Thread.sleep(200)
        }
    }
}