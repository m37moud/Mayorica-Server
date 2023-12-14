package com.example.di
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module


@Module(includes = [ApiModule::class])
@ComponentScan("com.example.mayorca-server")
class AppModule