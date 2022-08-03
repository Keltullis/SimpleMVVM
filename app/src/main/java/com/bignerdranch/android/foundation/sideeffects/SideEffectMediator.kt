package com.bignerdranch.android.foundation.sideeffects

import com.bignerdranch.android.foundation.ActivityScopeViewModel
import com.bignerdranch.android.foundation.utils.MainThreadExecutor
import com.bignerdranch.android.foundation.utils.ResourceActions
import java.util.concurrent.Executor

open class SideEffectMediator<Implementation>(
    executor: Executor = MainThreadExecutor()
) {

    protected val target = ResourceActions<Implementation>(executor)

    /**
     * Assign/unassign the target implementation for this mediator.
     */
    fun setTarget(target: Implementation?) {
        this.target.resource = target
    }

    fun clear() {
        target.clear()
    }

}