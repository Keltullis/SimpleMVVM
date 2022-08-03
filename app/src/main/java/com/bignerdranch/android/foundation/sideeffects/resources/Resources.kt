package com.bignerdranch.android.foundation.sideeffects.resources

import androidx.annotation.StringRes
import com.bignerdranch.android.foundation.sideeffects.resources.plugin.ResourcesPlugin

interface Resources {

    fun getString(@StringRes resId: Int, vararg args: Any): String

}