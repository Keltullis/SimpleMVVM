package com.bignerdranch.android.foundation.model.coroutines

import com.bignerdranch.android.foundation.model.ErrorResult
import com.bignerdranch.android.foundation.model.FinalResult
import com.bignerdranch.android.foundation.model.SuccessResult
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.util.concurrent.atomic.AtomicBoolean

fun <T> CancellableContinuation<T>.toEmitter(): Emitter<T> {

    return object : Emitter<T> {

        var done = AtomicBoolean(false)

        override fun emit(finalResult: FinalResult<T>) {
            if (done.compareAndSet(false, true)) {
                when (finalResult) {
                    is SuccessResult -> resume(finalResult.data)
                    is ErrorResult -> resumeWithException(finalResult.exception)
                }
            }
        }

        override fun setCancelListener(cancelListener: CancelListener) {
            invokeOnCancellation { cancelListener() }
        }
    }

}