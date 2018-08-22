package com.zilla

import android.app.Application
import javax.inject.Inject

class Application : Application() {
    @Inject
    lateinit var component: DaggerComponent

    override fun onCreate() {
        super.onCreate()

        DaggerDaggerComponent.builder()
                .funkyModule(FunkyModule(this))
                .build()
                .inject(this)
    }
}
