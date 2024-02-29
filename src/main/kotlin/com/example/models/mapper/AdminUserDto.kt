package com.example.models.mapper

import com.example.models.AdminUser
import com.example.models.AdminUserDetail
import com.example.models.UserInfo
import com.example.models.request.auth.AdminRegister
import com.example.models.response.UserAdminResponse
import com.example.security.hash.SaltedHash

fun AdminRegister.toModel() = UserInfo(
    fullName = this.full_name,
    username = this.username,
//    email = this.email,
//    phone = this.phone
)

fun UserInfo.toModel(saltedHash: SaltedHash) = AdminUser(
    full_name = this.fullName,
    username = this.username,
//    email = this.email,
//    phone = this.phone,
    password = saltedHash.hash,
    salt = saltedHash.salt,

    )

fun AdminUser.toModel() = AdminUserDetail(
    id = id,
    full_name = full_name,
    username = username,
//    email = this.email,
//    phone = this.phone,
    role = role,
    created_at = created_at,
    updated_at = updated_at
)

fun List<AdminUser>.toModel() = map { it.toModel() }


fun AdminUserDetail.toDto(id: Int) = UserAdminResponse(
    id = id,
    username =username ,
    fullName =full_name ,
    role = role,
    createdAt = created_at,

)



