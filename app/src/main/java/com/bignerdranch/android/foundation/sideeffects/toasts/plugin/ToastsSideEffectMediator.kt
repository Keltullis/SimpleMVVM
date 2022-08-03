package com.bignerdranch.android.foundation.sideeffects.toasts.plugin

import android.content.Context
import android.widget.Toast
import com.bignerdranch.android.foundation.utils.MainThreadExecutor
import com.bignerdranch.android.foundation.sideeffects.SideEffectMediator
import com.bignerdranch.android.foundation.sideeffects.toasts.Toasts

class ToastsSideEffectMediator(
    private val appContext: Context
) : SideEffectMediator<Nothing>(), Toasts {

    private val executor = MainThreadExecutor()

    override fun toast(message: String) {
        executor.execute {
            Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}