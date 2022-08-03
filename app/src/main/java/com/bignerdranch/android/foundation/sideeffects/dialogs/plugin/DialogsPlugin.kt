package com.bignerdranch.android.foundation.sideeffects.dialogs.plugin

import android.content.Context
import com.bignerdranch.android.foundation.sideeffects.SideEffectMediator
import com.bignerdranch.android.foundation.sideeffects.SideEffectPlugin
import com.bignerdranch.android.foundation.sideeffects.dialogs.Dialogs

class DialogsPlugin : SideEffectPlugin<DialogsSideEffectMediator, DialogsSideEffectImpl> {

    override val mediatorClass: Class<DialogsSideEffectMediator>
        get() = DialogsSideEffectMediator::class.java

    override fun createMediator(applicationContext: Context): SideEffectMediator<DialogsSideEffectImpl> {
        return DialogsSideEffectMediator()
    }

    override fun createImplementation(mediator: DialogsSideEffectMediator): DialogsSideEffectImpl {
        return DialogsSideEffectImpl(mediator.retainedState)
    }
}