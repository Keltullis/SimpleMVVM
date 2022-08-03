package com.bignerdranch.android.foundation.sideeffects.intents.plugin

import android.content.Context
import com.bignerdranch.android.foundation.sideeffects.SideEffectMediator
import com.bignerdranch.android.foundation.sideeffects.SideEffectPlugin
import com.bignerdranch.android.foundation.sideeffects.intents.Intents

class IntentsPlugin : SideEffectPlugin<Intents, Nothing> {

    override val mediatorClass: Class<Intents>
        get() = Intents::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<Nothing> {
        return IntentsSideEffectMediator(applicationContext)
    }

}