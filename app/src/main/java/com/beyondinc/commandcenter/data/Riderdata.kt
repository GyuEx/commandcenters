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
    var latitude: String? = "0.0"
    var longitude: String? = "0.0"
    var ModDT : String? = "1900-01-01 00:00:00"
    var assignCount : Int = 0
    var pickupCount : Int = 0
    var completeCount : Int = 0
    var MakerID: String? = ""
}