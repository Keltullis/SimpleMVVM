package com.bignerdranch.android.simplemvvm

import com.bignerdranch.android.foundation.SingletonScopeDependencies
import com.bignerdranch.android.foundation.model.coroutines.IoDispatcher
import com.bignerdranch.android.foundation.model.coroutines.WorkerDispatcher
import com.bignerdranch.android.simplemvvm.model.colors.InMemoryColorsRepository

object Initializer {

    fun initDependencies(){
        SingletonScopeDependencies.init { applicationContext ->

            val ioDispatcher = IoDispatcher()
            val workerDispatcher = WorkerDispatcher()

            return@init listOf(
                ioDispatcher,
                workerDispatcher,
                InMemoryColorsRepository(ioDispatcher)
            )
        }
    }

}