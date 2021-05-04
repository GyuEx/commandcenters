package com.beyondinc.commandcenter.net

import android.os.AsyncTask
import android.util.Log
import com.beyondinc.commandcenter.util.Crypto
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
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

class httpSub : AsyncTask<LatLng, Void, ArrayList<LatLng>>() {

    override fun doInBackground(vararg params: LatLng): ArrayList<LatLng> {

        var Agency = params[0]
        var Customer = params[1]

        Log.e("Poi" , Agency.latitude.toString() + " // " + Agency.longitude.toString())

        val url = URL("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=${Agency.longitude},${Agency.latitude}&goal=${Customer.longitude},${Customer.latitude}&option=trafast")
        val con: HttpURLConnection = url.openConnection() as HttpURLConnection
        con.connectTimeout = 3000 //서버에 연결되는 Timeout 시간 설정
        con.readTimeout = 3000 // InputStream 읽어 오는 Timeout 시간 설정
        con.setRequestProperty("X-NCP-APIGW-API-KEY-ID","150iwqv2oa")
        con.setRequestProperty("X-NCP-APIGW-API-KEY","Wnp6JA8z8OLujsYMkIfzcaxsBEBoQmQgwOKGDBVs")
        con.requestMethod = "GET"
        con.connect()

        val sb = StringBuilder()
        val br = BufferedReader(InputStreamReader(con.inputStream, "utf-8"))
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line).append("\n")
        }
        br.close()

        val parser = JSONParser()
        val results: JSONObject = parser.parse(sb.toString()) as JSONObject
        val route: JSONObject = results["route"] as JSONObject
        val trafast: JSONArray = route["trafast"] as JSONArray
        var a = makeResponseData(trafast)

        return a
    }

    fun makeResponseData(responseData: JSONArray): ArrayList<LatLng> {
        val returnData: ArrayList<LatLng> = ArrayList()
        for (block in responseData.indices) {
            val joMethod = responseData[block] as JSONObject
            val jaData = joMethod["path"] as JSONArray?
            if (null != jaData) {
                val map = ArrayList<LatLng>()
                for (j in jaData.indices) {
                    val joData = jaData[j] as JSONArray
                    val agencyPosition = LatLng(joData[1].toString().toDouble(), joData[0].toString().toDouble())
                    map.add(agencyPosition)
                }
                returnData.addAll(map)
            }
        }
        return returnData
    }
}