package com.bignerdranch.android.foundation.model.coroutines

import com.bignerdranch.android.foundation.model.FinalResult
import kotlinx.coroutines.CancellableContinuation

typealias CancelListener = () -> Unit

interface Emitter<T> {

    fun emit(finalResult: FinalResult<T>)


    fun setCancelListener(cancelListener: CancelListener)

    companion object {

        fun <T> wrap(emitter: Emitter<T>, onFinish: () -> Unit): Emitter<T> {
            return object : Emitter<T> {
                override fun emit(finalResult: FinalResult<T>) {
                    onFinish()
                    emitter.emit(finalResult)
                }

                override fun setCancelListener(cancelListener: CancelListener) {
                    emitter.setCancelListener {
                        onFinish()
                        cancelListener()
                    }
                }
            }
        }
    }
}