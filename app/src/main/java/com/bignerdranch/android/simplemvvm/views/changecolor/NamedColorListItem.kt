package com.bignerdranch.android.simplemvvm.views.changecolor

import com.bignerdranch.android.simplemvvm.model.colors.NamedColor

data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)