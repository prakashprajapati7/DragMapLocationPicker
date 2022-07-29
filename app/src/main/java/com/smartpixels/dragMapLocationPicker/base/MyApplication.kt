package com.smartpixels.dragMapLocationPicker.base

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTrimMemory(level: Int) {
        when (level) {
            TRIM_MEMORY_BACKGROUND,
            TRIM_MEMORY_MODERATE,
            TRIM_MEMORY_COMPLETE -> {
                /*
                   Release as much memory as the process can.
                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
            }
            else -> {  /*do nothing*/
            }
        }
        super.onTrimMemory(level)
    }
}
