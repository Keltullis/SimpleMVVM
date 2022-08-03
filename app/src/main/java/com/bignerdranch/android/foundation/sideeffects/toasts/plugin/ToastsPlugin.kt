package com.bignerdranch.android.foundation.sideeffects.toasts.plugin

import android.content.Context
import com.bignerdranch.android.foundation.sideeffects.SideEffectMediator
import com.bignerdranch.android.foundation.sideeffects.SideEffectPlugin
import com.bignerdranch.android.foundation.sideeffects.toasts.Toasts

class ToastsPlugin : SideEffectPlugin<Toasts, Nothing> {

    override val mediatorClass: Class<Toasts>
        get() = Toasts::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return ToastsSideEffectMediator(applicationContext)
    }

}