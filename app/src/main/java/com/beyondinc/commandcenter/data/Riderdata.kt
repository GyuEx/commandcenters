package com.beyondinc.commandcenter.repository.database.entity

import com.beyondinc.commandcenter.util.Codes

class Riderdata
{
    var id: String ? = ""
    var name: String ? = ""
    var centerID: String ? = ""
    var phoneNumber: String ? = ""
    var workingStateCode: String = Codes.RIDER_OFF_WORK
    var isEatTime: String = "N"
    var latitude: Double? = null
    var longitude: Double? = null
}