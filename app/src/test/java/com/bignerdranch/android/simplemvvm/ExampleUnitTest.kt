package com.bignerdranch.android.simplemvvm

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testFlow() = runBlocking {
        // Это coldFlow,он не запустится без терминальной операции
        // холодный флоу создаётся с помощью flowOf()/.asFlow/flow{}

        // горячий flow всегда в активном состояние,его не нужно запускать терминальным оператором
        // создаётся с помощью .shareIn/MutableSharedFlow() и .stateIn/MutableStateFlow()
        val flow:Flow<Int> = flowOf(1,2,3,4,5,6,7,8,9,10)

        // Привожу последовательность во флоу
        val numbers = 1..10
        val flow1:Flow<Int> = numbers.asFlow()

        println("Printing only even numbers multiplied by 10: ")

        flow
            .filter { it % 2 == 0 }
            .map { it * 10 }
            .collect {
                println(it)
            }
        // collect это терминальный(запускающий и слущающий) оператор

        println("Printing only odd numbers: ")

        flow
            .filter { it % 2 == 1 }
            .collect {
                println(it)
            }

    }
}