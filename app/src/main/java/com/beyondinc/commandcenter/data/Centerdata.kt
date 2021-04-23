package com.beyondinc.commandcenter.repository.database.entity

class Centerdata
{
    var centerId: String = ""                     // 센터 ID
    var centerName: String = ""               // 센터 이름 (서버등록이름)
    var companyId: String = ""           // 소속사 ID
    var companyName: String = ""            // 소속사 이름
    var sharedCenterIDs: String = ""       // 공유센터 ID셋 (TypeConverter 이용)
    var nickName: String = ""          // 센터약칭
    var isVisible: Boolean = true      // 센터정보 표시 여부
}