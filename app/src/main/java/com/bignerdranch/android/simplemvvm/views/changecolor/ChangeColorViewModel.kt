package com.bignerdranch.android.simplemvvm.views.changecolor

import androidx.lifecycle.*
import com.bignerdranch.android.foundation.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.bignerdranch.android.foundation.sideeffects.navigator.Navigator
import com.bignerdranch.android.foundation.sideeffects.resources.Resources
import com.bignerdranch.android.foundation.sideeffects.toasts.Toasts
import com.bignerdranch.android.foundation.utils.finiteShareIn
import com.bignerdranch.android.foundation.views.BaseViewModel
import com.bignerdranch.android.simplemvvm.R
import com.bignerdranch.android.simplemvvm.model.colors.ColorsRepository
import com.bignerdranch.android.simplemvvm.model.colors.NamedColor
import com.bignerdranch.android.simplemvvm.views.changecolor.ChangeColorFragment.Screen
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(), ColorsAdapter.Listener {

    // input sources
    private val _availableColors = MutableStateFlow<Result<List<NamedColor>>>(PendingResult())
    private val _currentColorId = savedStateHandle.getStateFlow("currentColorId", screen.currentColorId)
    private val _instantSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)
    private val _sampledSaveInProgress = MutableStateFlow<Progress>(EmptyProgress)

    // main destination (contains merged values from _availableColors & _currentColorId)

    val viewState: Flow<Result<ViewState>> = combine(
        _availableColors,
        _currentColorId,
        _instantSaveInProgress,
        _sampledSaveInProgress,
        ::mergeSources
    )

    val screenTitle: LiveData<String> = viewState.map { result ->
        return@map if (result is SuccessResult) {
            val currentColor = result.data.colorsList.first { it.selected }
            resources.getString(R.string.change_color_screen_title, currentColor.namedColor.name)
        } else {
            resources.getString(R.string.change_color_screen_title_simple)
        }
    }.asLiveData()

    init {
        load()
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_instantSaveInProgress.value.isInProgress()) return
        _currentColorId.value = namedColor.id
    }

    fun onSavePressed() = viewModelScope.launch {
        try {
            _instantSaveInProgress.value = PercentageProgress.START
            _sampledSaveInProgress.value = PercentageProgress.START
            val currentColorId = _currentColorId.value
            val currentColor = colorsRepository.getById(currentColorId)

            val flow = colorsRepository.setCurrentColor(currentColor).finiteShareIn(this)

            val instantJob = async {
                flow.collect { percentage ->
                    _instantSaveInProgress.value = PercentageProgress(percentage)
                }
            }

            val sampledJob = async {
                flow.sample(200)
                    .collect { percentage ->
                        _sampledSaveInProgress.value = PercentageProgress(percentage)
                    }
            }

            instantJob.await()
            sampledJob.await()

            navigator.goBack(currentColor)

            // слушаем флоу и отрисовываем результат
            //colorsRepository.setCurrentColor(currentColor).collect{ percentage ->
            //    _instantSaveInProgress.value = PercentageProgress(percentage)
            //}
        } catch (e: Exception) {
            if (e !is CancellationException) toasts.toast(resources.getString(R.string.error_happened))
        } finally {
            _instantSaveInProgress.value = EmptyProgress
            _sampledSaveInProgress.value = EmptyProgress
        }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    fun tryAgain() {
        load()
    }


    private fun mergeSources(colors:Result<List<NamedColor>>, currentColorId:Long,
                             instanceSaveInProgress:Progress, sampleSaveInProgress: Progress):Result<ViewState> {
        // map Result<List<NamedColor>> to Result<ViewState>
        return colors.map { colorsList ->
            ViewState(
                // map List<NamedColor> to List<NamedColorListItem>
                colorsList = colorsList.map { NamedColorListItem(it, currentColorId == it.id) },

                showSaveButton = !instanceSaveInProgress.isInProgress(),
                showCancelButton = !instanceSaveInProgress.isInProgress(),
                showSaveProgressBar = instanceSaveInProgress.isInProgress(),
                saveProgressPercentage = instanceSaveInProgress.getPercentage(),
                // Здесь мы добираемся до ресурса, а вторым параметром подставляем значение вместо %1$d
                saveProgressPercentageMessage = resources.getString(R.string.percentage_value,sampleSaveInProgress.getPercentage())
            )
        }
    }

    private fun load() = into(_availableColors) { colorsRepository.getAvailableColors() }

    data class ViewState(
        val colorsList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean,
        val saveProgressPercentage:Int,
        val saveProgressPercentageMessage:String
    )

}