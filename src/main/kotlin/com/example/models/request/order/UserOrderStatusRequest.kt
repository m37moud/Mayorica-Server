package com.example.models.request.order

data class UserOrderStatusRequest(
    val approveState: Int ,
    val approveDate: String ,
    val approveUpdateDate: String,
    val approveByAdminId: Int ,
    val totalAmount: Double,
    val takenAmount: Double ,
    val availableAmount: Double ,
    val note: String
){

}
