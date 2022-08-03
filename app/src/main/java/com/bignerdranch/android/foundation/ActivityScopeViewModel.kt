package com.bignerdranch.android.foundation

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.foundation.sideeffects.SideEffectMediator
import com.bignerdranch.android.foundation.sideeffects.SideEffectMediatorsHolder


const val ARG_SCREEN = "ARG_SCREEN"


class ActivityScopeViewModel : ViewModel() {

    internal val sideEffectMediatorsHolder = SideEffectMediatorsHolder()

    // contains the list of side-effect mediators that can be
    // passed to view-model constructors
    val sideEffectMediators: List<SideEffectMediator<*>>
        get() = sideEffectMediatorsHolder.mediators

    override fun onCleared() {
        super.onCleared()
        sideEffectMediatorsHolder.clear()
    }

}