package com.beyondinc.commandcenter.net

import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.util.Crypto
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
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HttpConn : Thread(), ThreadFun {

    var isKeep : Boolean = false

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

    fun makeResponseData(responseData: JSONArray): ArrayList<HashMap<String, String>> {
        val returnData: ArrayList<HashMap<String, String>> = ArrayList()

        for (block in responseData.indices) {
            val joMethod = responseData[block] as JSONObject
            val jaData = joMethod["data"] as JSONArray?
            if (null != jaData) {
                for (j in jaData.indices) {
                    val joData = jaData[j] as JSONObject
                    val map = HashMap<String, String>()
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

                    val code = jsonMessage.keys.iterator().next()
                    val data = jsonMessage[code]

                    Log.e("Connect", "" + code)
                    Log.e("Connect", "" + data)

                    val requestCipherKey = Crypto.generateMD5Hash("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3b29kZWwiLCJVc2VySWQiOiJ3b29kZWwiLCJQYXNzd2QiOiJhcHBrZXkiLCJHcmFudFR5cGUiOiJydyIsIlNlcnZpY2VOYW1lIjoid29vZGVsIiwiQWNjZXNzRXhwaXJlRFQiOiIyMTIwLTA5LTA2IDEyOjQ4OjM2In0.Gf0U5k_3vCbcb0O6xOeetwnk66N8HiJwFe_iQ-Ri8iCZRi2DDHns3GYWnk9Xf1vteuu3AJQ13iOEQaq1N99ZCA")!!.toLowerCase(Locale.getDefault())
                    val responseCipherKey = Crypto.generateMD5Hash(Crypto.getCurrentTimeKey())!!.toLowerCase(Locale.getDefault())
                    val requestJson = makeRequestJSON(responseCipherKey,code,data!!)
                    val requestPlainString = requestJson.toString()
                    val requestBody = Crypto.AES256.encrypt(requestPlainString, requestCipherKey)
                    var receiveDoc = StringBuilder()

                    Log.e("ResultSand", "" + requestPlainString)

                    val url = URL("https://dev.stds.co.kr:8443/byservice/ManagerAppCipherConnect.action")
                    val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                    con.connectTimeout = 3000 //서버에 연결되는 Timeout 시간 설정
                    con.readTimeout = 30000 // InputStream 읽어 오는 Timeout 시간 설정
                    con.setRequestProperty("UserAddData", "2")
                    con.setRequestProperty("content-type", "application/json; charset=utf-8")
                    con.setRequestProperty("authorization", "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ3b29kZWwiLCJVc2VySWQiOiJ3b29kZWwiLCJQYXNzd2QiOiJhcHBrZXkiLCJHcmFudFR5cGUiOiJydyIsIlNlcnZpY2VOYW1lIjoid29vZGVsIiwiQWNjZXNzRXhwaXJlRFQiOiIyMTIwLTA5LTA2IDEyOjQ4OjM2In0.Gf0U5k_3vCbcb0O6xOeetwnk66N8HiJwFe_iQ-Ri8iCZRi2DDHns3GYWnk9Xf1vteuu3AJQ13iOEQaq1N99ZCA")
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

                        var temp : HashMap<String,ArrayList<HashMap<String, String>>> = HashMap()
                        temp.put(code, returnData)
                        Vars.receiveList.add(temp)

                    } else {
                        System.out.println(con.getResponseMessage())
                    }
                    Thread.sleep(50)
                }

            } catch (e: Exception) {
                System.err.println(e.toString())
            }
        }
    }
}