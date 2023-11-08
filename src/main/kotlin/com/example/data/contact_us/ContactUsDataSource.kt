package com.example.data.contact_us

import com.example.models.ContactUs

interface ContactUsDataSource {

    suspend fun getContactUsInfo() : ContactUs?
    suspend fun addContactUsInfo(contactUs: ContactUs) : Int
    suspend fun updateContactUsInfo(contactUs:ContactUs) : Int
    suspend fun deleteContactUsInfo() : Int

}