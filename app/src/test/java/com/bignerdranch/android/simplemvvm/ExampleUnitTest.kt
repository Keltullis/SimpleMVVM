package com.bignerdranch.android.simplemvvm

import android.util.Log
import com.bignerdranch.android.simplemvvm.model.colors.ColorListener
import com.bignerdranch.android.simplemvvm.model.colors.NamedColor
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import org.junit.Test

import org.junit.Assert.*
import kotlin.coroutines.coroutineContext

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
        // Работаем в корутине
        // Это coldFlow,он не запустится без терминальной операции
        val flow:Flow<Int> = flowOf(1,2,3,4,5,6,7,8,9,10)

        // Преобразую последовательность во флоу
        val numbers = 1..10
        val flow1:Flow<Int> = numbers.asFlow()
        flow1.collect {
            println(it)
        }

        println("Printing only even numbers multiplied by 10: ")
        flow
            .filter { it % 2 == 0 }
            .map { it * 10 }
            .collect {
                // После вызова collect флоу начал испускать элементы,они прошли через фильтр и мап, в collect мы их последовательно получали и печатали
                println(it)
            }
        // collect это терминальный(запускающий и слушающий) оператор
        // 20
        // 40
        // 60
        // 80
        // 100

        println("Printing only odd numbers: ")

        flow
            .filter { it % 2 == 1 }
            .collect {
                println(it)
            }
        // Этот collect тоже получит изначальный набор чисел и выведет нечётные 13579
    }


    @Test
    fun testFlow2(complete:Boolean):Flow<Int> = flow{
        if(!complete){
            var progress = 0
            while (progress < 100){
                progress += 2
                delay(30)
                // метод emit публикует текущий прогресс
                emit(progress)
            }
        } else{
            emit(100)
        }
    // flowOn переводит флоу в другой поток(ввода вывода),но это не обязательно,и без этой штуки запустится
    // но мы должны вызвать testFlow2.collect,он уже будет публиковать прогресс,без collect не запустится
    }.flowOn(Dispatchers.IO)


    // Это замена слушателям,возвращает бесконечный флоу,который удаляет слушатель только когда закончится корутина в вью модели
    @Test
    fun callbackFlow1():Flow<NamedColor> = callbackFlow {
        val listeners = mutableSetOf<ColorListener>()
        val listener:ColorListener = {
            //Передаём текущий цвет во flow
            trySend(it)
        }
        listeners.add(listener)
        // Останавливает выполнение на этом месте и ждёт завершения внешней корутины
        awaitClose {
            listeners.remove(listener)
        }
    }.buffer(Channel.CONFLATED)
    // Определяет поведение буфера

    fun testCallback(){
        // По скольку флоу возвращаемый этой функцией является бесконечным,поэтому мы всё время будем слушать результат
        // Потом когда она отменится,сработает awaitClose и всё почистится
        GlobalScope.launch {
            // Сюда будет приходить текущий цвет
            callbackFlow1().collect {
                // do something
            }
        }
    }
    //--- Hot Flow ,а именно State Flow (работает почти так же как и livedata)
    // Обязательно должно быть какое то значение
    val testStateFlow = MutableStateFlow(1)
    // Потом где то мы подписываемся на это значение и в collect{ тут обрабатываем } обрабатываем
    // этот флоу будет рассылать данные даже если его никто не слушает

    //--- SharedFlow
    // бесконечный флоу,отличается от StateFlow тем что можно настроить буфер
    // создаётся с помощью MutableSharedFlow(replay:Int,extraBufferCapacity:Int,onBufferOverflow = SUSPEND|DROP_LATEST|DROP_OLDEST)
    // либо Flow<T>.buffer(..).shareIn(..)
    // buffer(bufferCapacity,onBufferOverflow) - опциональный оператор
    // shareIn(scope:CoroutineScope,started = Eagerly|Lazily|WhiteSubscribed(stopTimeoutMillis,replayExpirationMillis),replay:Int = 0)
    // replay - определяет колличество элементов которые будут хранится внутри sharedFlow для новых подписчииков
    // если поставить значение 3,кто то потом подпишется на рассылку,он получит последние 3 элемента и последующие тоже дойдут
    // extraBufferCapacity - это дополнительное место в буфере,поверх того что указано в реплэй,максимальный размер буфера равен сумме реплэй и экстрабафера
    // то что находится в баферкапасити не будет приходить новым подписчикам
    // SUSPEND - тогда тот кто отправивил элемент заснёт до тех пор пока не появится место в буфере,либо получит уведомление о том что пока нельзя
    // DROP OLDEST ИЛИ LATEST то отправка элемента будет всегда успешна,при этом если олдест,то старый самый элемент будет выброшен из буфера и новый займёт его место
    // а если лэйтест то наоборот,то текущий элемент который попытались отправить,сам будет выброшен
    // в итоге на этот flow тоже кто-то подписывается и флоу отправляет ему изменившуюся инфу

//-------------------------------------
// Flow
// Это поток элементов, причём он асинхронный, между элементами могут быть какие угодно задержки(работает в корутине)
// поток может быть конечным и бесконечным,если он конечный то на выходе мы получим сигнал что flow завершен,а бесконечный либо никогда не завершится либо произойдёт ошибка
// Терминальный(завершающий) операторы стартуют выполнение Cold Flow  или же подписываются на Hot Flow
// например collect, reduce/fold, toList/toSet
// так же есть обычные операторы для преобразования Flow
// например map,filter,drop,take и тд
// если флоу холодный,то терминальный оператор его стартует,если он горячий,то оператор на него подписывается
// Существует всего 2 типа Flow,cold(изначально неактивен) и hot(изначально активен,даже если на него не подписаны)
// Cold Flow создаются с помощью Flow Builders:преобразование уже существующих элементов - flowOf(...)/.asFlow()
//                                                                      создание вручную - flow{}, callbackFlow{}/channelFlow{}
// Hot Flow создаются с помощью операторов или методов-конструкторов: .shareIn/MutableSharedFlow()
//                                                                    .stateIn/MutableStateFlow()
// emit() - отправляет данные, collect() - принимает
//-------------------------------------



//-------------------------------------
// Coroutine

    @Test
    fun testCoroutine(){
        // Запускаем корутину
        GlobalScope.launch {
            delay(1000)
            // Переносим выполнение корутины в другой поток через withContext(здесь указывается поток)
            val result = withContext(Dispatchers.Default){
                //запускаем 3 ассинхронных операции
                val part1 = async {
                    delay(1000)
                    return@async "Part 1 done"
                }
                val part2 = async {
                    delay(2000)
                    return@async "Part 2 done"
                }
                val part3 = async {
                    delay(3000)
                    return@async "Part 3 done"
                }
                // Дожидаемся результатов(общее время выполнения работы будет 3 секунды)
                val result1 = part1.await()
                val result2 = part2.await()
                val result3 = part3.await()

                // Возвращаем результат
                return@withContext "$result1\n$result2\n$result3"
            }
            println(result)
        }
        // в переменной result будет готовый результат
    }
}
// Корутины запускаются методами launch - ничего не возвращает,async - может что то вернуть и withContext()
// метод join() позволяет подождать завершения в launch,метод await()  позволяет дождаться результата выполнения в async
// withContext() используется внутри короутины,он переопределяет какие то характеристики контекста,чаще всего диспатчер(меняет поток например из главного в другой)
// они запускаются на coroutineScope{} или viewModelScope. или же GlobalScope. или же lifeCycleScope
// viewModelScope.launch{ delay(1000) }
// delay это suspend функция,она может быть приостановленна
// block: suspend () -> -это лямбда для корутины
// ещё 1 пример возвращения чего либо с задержкой
// suspend fun getColor():Color{
//     delay(1000)
//     return current color
//  }


// Пример с измением потока на поток для ввода вывода(есть ещё диспатчер для тяжелых операций(вычислений))
// suspend fun getColor():Color = withContext(Dispatchers.IO){
//     delay(1000)
//     return@withContext current color
//  }