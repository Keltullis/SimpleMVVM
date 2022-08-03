package com.bignerdranch.android.foundation.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class MainThreadExecutor : Executor {

    private val handler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        if (Looper.getMainLooper().thread.id == Thread.currentThread().id) {
            command.run()
        } else {
            handler.post(command)
        }
    }

}