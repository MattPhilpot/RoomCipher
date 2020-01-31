package com.philpot.roomcipher.util

import android.util.Log
import com.philpot.roomcipher.BuildConfig

object ExceptionUtil {
    fun maybeLog(tag: String, t: Throwable) {
        val message = Log.getStackTraceString(t)
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, t)
        }
    }
}
