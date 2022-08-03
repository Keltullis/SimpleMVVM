package com.bignerdranch.android.simplemvvm.views

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bignerdranch.android.foundation.model.Result
import com.bignerdranch.android.foundation.views.BaseFragment
import com.bignerdranch.android.simplemvvm.R
import com.bignerdranch.android.simplemvvm.databinding.PartResultBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


fun <T> BaseFragment.renderSimpleResult(root:ViewGroup, result:Result<T>, onSuccess:(T) -> Unit){
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onPending = {
            binding.progressBar.visibility = View.VISIBLE
        },
        onError = {
            binding.errorContainer.visibility = View.VISIBLE
        },
        onSuccess = { successData ->
            root.children
                .filter { it.id != R.id.progressBar && it.id != R.id.errorContainer }
                .forEach { it.visibility = View.VISIBLE }
            onSuccess(successData)
        }
    )
}

fun <T> BaseFragment.collectFlow(flow: Flow<T>, onCollect: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        // this coroutine is cancelled in onDestroyView
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            // this coroutine is launched every time when onStart is called;
            // collecting is cancelled in onStop
            flow.collect {
                onCollect(it)
            }
        }
    }
}

fun BaseFragment.onTryAgain(root:View, onTryAgainPressed: () -> Unit){
    root.findViewById<Button>(R.id.tryAgainButton).setOnClickListener { onTryAgainPressed() }
}