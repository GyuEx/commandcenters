package com.beyondinc.commandcenter.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beyondinc.commandcenter.Interface.LoginsFun
import com.beyondinc.commandcenter.Interface.SettingFun
import com.beyondinc.commandcenter.data.Logindata
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.MakeJsonParam
import com.beyondinc.commandcenter.util.Procedures
import com.beyondinc.commandcenter.util.Vars
import org.json.simple.JSONArray
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePasswordViewModel : ViewModel() {

    var retropassword : MutableLiveData<String> = MutableLiveData() // 구비밀번호
    var newtropassword : MutableLiveData<String> = MutableLiveData() // 신규비밀번호
    var repassword : MutableLiveData<String> = MutableLiveData() // 재비밀번호
    var titletext : MutableLiveData<String> = MutableLiveData() // 타이틀 텍스트

    init {
        newtropassword.value = ""
        retropassword.value = ""
        repassword.value = ""
    }

    fun click_change_finish(){

        if(retropassword.value != Logindata.LoginPw)
        {
            Toast.makeText(Vars.lContext,"비밀번호가 맞지 않습니다.", Toast.LENGTH_LONG).show()
        }
        else if (validate())
        {
            var temp: ConcurrentHashMap<String, JSONArray> = ConcurrentHashMap()
            temp[Procedures.LOGIN] = MakeJsonParam().makeChangePasswordParameter(Logindata.LoginId.toString(), retropassword.value.toString(),newtropassword.value.toString())
            Vars.sendList.add(temp)
        }
    }

    fun validate() : Boolean {
        if (retropassword.value!!.isEmpty()) {
            Toast.makeText(Vars.lContext,"비밀번호를 입력하세요.",Toast.LENGTH_LONG).show()
            return false
        }
        if (newtropassword.value!!.isEmpty()) {
            Toast.makeText(Vars.lContext,"새 비밀번호를 입력하세요.",Toast.LENGTH_LONG).show()
            return false
        }
        if (repassword.value!!.isEmpty()) {
            Toast.makeText(Vars.lContext,"새 비밀번호 확인을 입력하세요.",Toast.LENGTH_LONG).show()
            return false
        }
        if (newtropassword.value!!.length < 6) {
            Toast.makeText(Vars.lContext,"새 비밀번호는 6자리 이상 입력하세요",Toast.LENGTH_LONG).show()
            return false
        }
        if (!PasswordValidation(newtropassword.value!!.toString())) {
            Toast.makeText(Vars.lContext,"비밀번호는 6자리이상이고, 문자,숫자,특수문자가 포함되어야 합니다.",Toast.LENGTH_LONG).show()
            return false
        }
        if (retropassword.value!!.toString() == newtropassword.value!!.toString()) {
            Toast.makeText(Vars.lContext,"변경전 비밀번호와 변경후 비밀번호가 같습니다. 다른 비밀번호를 입력하세요.",Toast.LENGTH_LONG).show()
            return false
        }
        if (newtropassword.value!!.toString() != repassword.value!!.toString()) {
            Toast.makeText(Vars.lContext,"새 비밀번호와 비밀번호 확인이 같지 않습니다.",Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun PasswordValidation(sPasswd: String): Boolean {
        // 영문자, 숫자, 특수문자 각1개이상 포함하고 6자이상일것
        var bRet = false
        try {
            var p: Pattern = Pattern.compile(".*[a-zA-Z].*") // 영문자 존재여부
            var m: Matcher = p.matcher(sPasswd)
            if (!m.matches()) return false
            p = Pattern.compile(".*[0-9].*") // 숫자 존재여부
            m = p.matcher(sPasswd)
            if (!m.matches()) return false
            p = Pattern.compile(".*[!@#$%^&*()].*") // 특수문자 존재여부
            m = p.matcher(sPasswd)
            if (!m.matches()) return false
            bRet = sPasswd.length >= 6
        } catch (e: Exception) {
            e.printStackTrace()
            bRet = false
        }
        return bRet
    }
}