package com.bignerdranch.android.foundation.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.bignerdranch.android.foundation.model.ErrorResult
import com.bignerdranch.android.foundation.model.PendingResult
import com.bignerdranch.android.foundation.model.Result
import com.bignerdranch.android.foundation.model.SuccessResult
import com.bignerdranch.android.foundation.views.activity.ActivityDelegateHolder


abstract class BaseFragment : Fragment() {

    /**
     * View-model that manages this fragment
     */
    abstract val viewModel: BaseViewModel

    /**
     * Call this method when activity controls (e.g. toolbar) should be re-rendered
     */
    fun notifyScreenUpdates() {
        (requireActivity() as ActivityDelegateHolder).delegate.notifyScreenUpdates()
    }

    fun <T> renderResult(root:ViewGroup,
                         result:Result<T>,
                         onPending:() -> Unit,
                         onError:(Exception) -> Unit,
                         onSuccess:(T) -> Unit){

        root.children.forEach {
            it.visibility = View.GONE
        }

        when(result){
            is SuccessResult -> onSuccess(result.data)
            is ErrorResult -> onError(result.exception)
            is PendingResult -> onPending()
        }
    }
}