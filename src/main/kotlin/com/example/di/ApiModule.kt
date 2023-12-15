package com.example.di

import com.example.config.AppConfig
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.ktorm.database.Database

@Module
class ApiModule {
    @Single
    fun provideDatabase(appConfig: AppConfig):Database{
        return Database.connect(
            url = appConfig.applicationConfiguration.propertyOrNull("database.database")?.getString() ?: "" ,//database,//"jdbc:mysql://localhost:3306/mayorica_db",
            driver = appConfig.applicationConfiguration.propertyOrNull("database.driver")?.getString()?: "", // "com.mysql.cj.jdbc.Driver",
            user = appConfig.applicationConfiguration.propertyOrNull("database.user")?.getString() ?: "", //"root",
            password = appConfig.applicationConfiguration.propertyOrNull("database.password")?.getString() ?: "", // "20102010"
        )
    }

}