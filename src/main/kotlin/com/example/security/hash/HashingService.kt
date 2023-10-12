package com.example.security.hash

interface HashingService {
    fun createHashingPassword(value :String , saltLength:Int = 32):SaltedHash
    fun verifyHashingPassword(value: String , saltedHash: SaltedHash):Boolean
}