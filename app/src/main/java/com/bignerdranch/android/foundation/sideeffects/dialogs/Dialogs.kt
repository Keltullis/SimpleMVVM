package com.bignerdranch.android.foundation.sideeffects.dialogs

import com.bignerdranch.android.foundation.sideeffects.dialogs.plugin.DialogConfig

interface Dialogs {

    suspend fun show(dialogConfig: DialogConfig): Boolean

}
