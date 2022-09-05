package com.bignerdranch.android.photogallery.di

import android.app.Application
import android.content.Context

class App : Application() {
    lateinit var appComponent: AppComponent

    val Context.appComponent : AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule())
            .build()
    }
}