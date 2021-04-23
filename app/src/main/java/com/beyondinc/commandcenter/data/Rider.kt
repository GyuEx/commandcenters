package com.beyondinc.commandcenter.repository.database.entity

import com.beyondinc.commandcenter.util.Codes

data class Rider(
        val id: String,
        val name: String,
        val centerID: String,
        val phoneNumber: String,
        var workingStateCode: String = Codes.RIDER_OFF_WORK,
        var isEatTime: Boolean = false,
        var latitude: Double? = null,
        var longitude: Double? = null,
)
