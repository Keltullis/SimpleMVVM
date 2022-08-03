package com.bignerdranch.android.foundation.sideeffects.navigator

import com.bignerdranch.android.foundation.views.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen)


    fun goBack(result: Any? = null)

}