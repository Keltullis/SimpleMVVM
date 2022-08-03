package com.bignerdranch.android.foundation.sideeffects.navigator.plugin

import com.bignerdranch.android.foundation.views.BaseScreen
import com.bignerdranch.android.foundation.sideeffects.SideEffectMediator
import com.bignerdranch.android.foundation.sideeffects.navigator.Navigator

class NavigatorSideEffectMediator : SideEffectMediator<Navigator>(), Navigator {

    override fun launch(screen: BaseScreen) = target {
        it.launch(screen)
    }

    override fun goBack(result: Any?) = target {
        it.goBack(result)
    }

}