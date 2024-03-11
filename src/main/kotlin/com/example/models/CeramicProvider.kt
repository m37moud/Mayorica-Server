package com.example.models

import kotlinx.serialization.Serializable
import java.text.DecimalFormat

@Serializable
data class CeramicProvider(
    val id: Int = -1,
    val userAdminID: Int = -1,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val city: String,
    val address: String,
    val createdAt: String = "",
    val updatedAt: String = ""

) {
    fun calculationByDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val Radius = 6371 // radius of earth in Km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2)))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))

        //return Radius * c
        return kmInDec
    }

}
