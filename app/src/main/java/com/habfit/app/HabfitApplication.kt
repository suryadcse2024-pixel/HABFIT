package com.habfit.app

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HabfitApplication : Application() {
    
    @Inject
    lateinit var analytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }
}
