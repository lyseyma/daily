package com.kh.daily

import android.app.Application
import com.google.firebase.FirebaseApp

class Daily : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
