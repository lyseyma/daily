package com.kh.daily

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Daily Application class.
 * This is the main application class that initializes global app components.
 *
 * Key responsibilities:
 * - Initialize Firebase for the entire application
 * - Set up any other global configurations needed at app startup
 */
class Daily : Application() {

    /**
     * Called when the application is created.
     * This is where we initialize Firebase and other global services.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase for the entire application
        FirebaseApp.initializeApp(this)
    }
}
