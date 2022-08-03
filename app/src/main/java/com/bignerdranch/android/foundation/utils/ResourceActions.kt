package com.bignerdranch.android.foundation.utils

import java.util.concurrent.Executor

typealias ResourceAction<T> = (T) -> Unit

class ResourceActions<T>(private val executor: Executor) {

    // Здесь находится MainActivity
    var resource: T? = null
        set(newValue) {
            field = newValue
            // Если активити не равна нулл,проверяем,есть ли действия ожидающие доступности этого ресурса
            if (newValue != null) {
                actions.forEach { action ->
                    executor.execute {
                        action(newValue)
                    }
                }
                actions.clear()
            }
        }

    private val actions = mutableListOf<ResourceAction<T>>()

    // Если (мэйн)активити записанная в resource существует,то запускаем функции,если нет,то записываем их в список
    // затем активити снова появляется и функции из списка выполняются
    operator fun invoke(action: ResourceAction<T>) {
        val resource = this.resource
        if (resource == null) {
            actions += action
        } else {
            action(resource)
        }
    }
    // Оператор инвок перегружен для удобства,это позволяет написать действие просто в фигурных скобках
    // иначе пришлось бы сделать какой то метод и писать ResourceActions.etotmetod(и тут описывать действия)

    fun clear() {
        actions.clear()
    }
}
