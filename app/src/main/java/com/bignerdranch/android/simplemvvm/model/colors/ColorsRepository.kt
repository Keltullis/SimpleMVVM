package com.bignerdranch.android.simplemvvm.model.colors

import com.bignerdranch.android.foundation.model.Repository
import kotlinx.coroutines.flow.Flow

typealias ColorListener = (NamedColor) -> Unit


interface ColorsRepository : Repository {

    suspend fun getAvailableColors(): List<NamedColor>

    suspend fun getById(id: Long): NamedColor

    suspend fun getCurrentColor(): NamedColor

    //suspend fun setCurrentColor(color: NamedColor)
    fun setCurrentColor(color: NamedColor):Flow<Int>

    fun listenCurrentColor():Flow<NamedColor>

}