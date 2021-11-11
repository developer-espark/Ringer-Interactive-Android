package com.ringer.interactive.activity

import android.app.Application
import com.gu.toolargetool.TooLargeTool

class Ring : Application() {

    override fun onCreate() {
        super.onCreate()
        TooLargeTool.startLogging(this)

    }

}