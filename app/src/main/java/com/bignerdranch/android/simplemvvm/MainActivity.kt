package com.bignerdranch.android.simplemvvm

import android.os.Bundle
import com.bignerdranch.android.foundation.sideeffects.navigator.plugin.StackFragmentNavigator
import com.bignerdranch.android.foundation.sideeffects.navigator.plugin.NavigatorPlugin
import com.bignerdranch.android.foundation.sideeffects.SideEffectPluginsManager
import com.bignerdranch.android.foundation.sideeffects.dialogs.plugin.DialogsPlugin
import com.bignerdranch.android.foundation.sideeffects.intents.plugin.IntentsPlugin
import com.bignerdranch.android.foundation.sideeffects.permissions.plugin.PermissionsPlugin
import com.bignerdranch.android.foundation.sideeffects.resources.plugin.ResourcesPlugin
import com.bignerdranch.android.foundation.sideeffects.toasts.plugin.ToastsPlugin
import com.bignerdranch.android.foundation.views.activity.BaseActivity
import com.bignerdranch.android.simplemvvm.views.currentcolor.CurrentColorFragment


class MainActivity : BaseActivity() {

    override fun registerPlugins(manager: SideEffectPluginsManager) = with (manager) {
        val navigator = createNavigator()
        register(ToastsPlugin())
        register(ResourcesPlugin())
        register(NavigatorPlugin(navigator))
        register(PermissionsPlugin())
        register(DialogsPlugin())
        register(IntentsPlugin())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Initializer.initDependencies()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun createNavigator() = StackFragmentNavigator(
        containerId = R.id.fragmentContainer,
        defaultTitle = getString(R.string.app_name),
        animations = StackFragmentNavigator.Animations(
            enterAnim = R.anim.enter,
            exitAnim = R.anim.exit,
            popEnterAnim = R.anim.pop_enter,
            popExitAnim = R.anim.pop_exit
        ),
        initialScreenCreator = { CurrentColorFragment.Screen() }
    )

}
// Корутины запускают код в новом потоке и даже ждут результат
// viewModelScope ,GlobalScope = скопы,на них запускается корутины
// launch,async,withContext(Dispatcher.IO) = методы запуска корутин
// async возвращает результат,launch нет,withContext(диспатчер) позволяет изменить контекст и выполнить код в том потоке который выберешь сам
// GlobalScope.launch{действие} = так выглядит корутина
// suspend fun test() приостанавливаемая функция которая может работать в корутине
// join() ждёт выполнения корутины, repeat(10){launch{}} запустит 10 сопрограмм которые что-то сделают
// cancel отменяет выполнение корутины
