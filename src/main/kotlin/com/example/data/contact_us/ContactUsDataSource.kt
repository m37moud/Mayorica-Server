package com.example.data.contact_us

import com.example.models.ContactUs
import com.example.models.ContactUsCreate

interface ContactUsDataSource {

    suspend fun getContactUsInfo(): ContactUs?
    suspend fun addContactUsInfo(contactUs: ContactUsCreate): ContactUs
    suspend fun updateContactUsInfo(id: Int, contactUs: ContactUsCreate): Int
    suspend fun deleteContactUsInfo(): Int

}