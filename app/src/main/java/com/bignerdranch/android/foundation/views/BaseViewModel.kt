package com.bignerdranch.android.foundation.views

import androidx.lifecycle.*
import kotlinx.coroutines.*
import com.bignerdranch.android.foundation.model.ErrorResult
import com.bignerdranch.android.foundation.model.Result
import com.bignerdranch.android.foundation.model.SuccessResult
import com.bignerdranch.android.foundation.utils.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Base class for all view-models.
 */
open class BaseViewModel : ViewModel() {

    private val coroutineContext = SupervisorJob() + Dispatchers.Main.immediate + CoroutineExceptionHandler { _, throwable ->
        // you can add some exception handling here
    }

    // custom scope which cancels jobs immediately when back button is pressed
    protected val viewModelScope = CoroutineScope(coroutineContext)
    // по мимо viewModelScope,существует GlobalScope

    override fun onCleared() {
        super.onCleared()
        clearScope()
    }

    /**
     * Override this method in child classes if you want to listen for results
     * from other screens
     */
    open fun onResult(result: Any) {

    }

    /**
     * Override this method in child classes if you want to control go-back behaviour.
     * Return `true` if you want to abort closing this screen
     */
    open fun onBackPressed(): Boolean {
        clearScope()
        return false
    }

    /**
     * Launch the specified suspending [block] and use its result as a valud for the
     * provided [liveResult].
     */
    fun <T> into(liveResult: MutableLiveResult<T>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                liveResult.postValue(SuccessResult(block()))
            } catch (e: Exception) {
                if (e !is CancellationException) liveResult.postValue(ErrorResult(e))
            }
        }
    }

    fun <T> into(stateFlow: MutableStateFlow<Result<T>>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                stateFlow.value = SuccessResult(block())
            } catch (e: Exception) {
                if (e !is CancellationException) stateFlow.value = ErrorResult(e)
            }
        }
    }


    fun <T> SavedStateHandle.getStateFlow(key:String, initialValue:T):MutableStateFlow<T>{
        val savedStateHandle = this
        // Берём значение по ключу
        val mutableFlow = MutableStateFlow(savedStateHandle[key] ?: initialValue)

        // как только в мютабл флоу что то запишут,мы тут же положим это в хэндл
        viewModelScope.launch {
            mutableFlow.collect {
                savedStateHandle[key] = it
            }
        }

        viewModelScope.launch {
            savedStateHandle.getLiveData<T>(key).asFlow().collect {
                mutableFlow.value = it
            }
        }

        return mutableFlow
    }

    private fun clearScope() {
        viewModelScope.cancel()
    }

}