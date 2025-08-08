/**
 * This file is a polygon for managed IO
 */
package dev.toolkt.reactive.managed_io

import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold

interface ProcessHandle {
    fun stop()
}

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class ProcedureFunction

typealias Schedule = Program<Any?>

interface Program<out A> {
    object Noop : Program<Unit> {
        override fun execute(): Pair<Unit, ProcessHandle> = Pair(
            Unit,
            object : ProcessHandle {
                override fun stop() {
                }
            },
        )
    }

    companion object {
        fun <A> prepare(
            build: () -> Program<A>,
        ): Program<A> = object : Program<A> {
            override fun execute(): Pair<A, ProcessHandle> {
                val program = build()

                return program.execute()
            }
        }

        fun parallel(
            programs: Iterable<Program<*>>,
        ): Schedule = object : AbstractSchedule() {
            override fun start(): ProcessHandle {
                val processHandles = programs.map { program ->
                    val (_, processHandle) = program.execute()
                    processHandle
                }

                return object : ProcessHandle {
                    override fun stop() {
                        processHandles.forEach { it.stop() }
                    }
                }
            }
        }

        fun parallel(
            vararg programs: Program<*>,
        ): Schedule = parallel(
            programs = programs.asIterable(),
        )
    }

    @ProcedureFunction
    fun execute(): Pair<A, ProcessHandle>
}

fun <A> Program<A>.spy(
    onExecute: () -> Unit,
    onStop: () -> Unit,
): Program<A> = object : Program<A> {
    override fun execute(): Pair<A, ProcessHandle> {
        onExecute()

        val (result, processHandle) = this@spy.execute()

        return Pair(
            result,
            object : ProcessHandle {
                override fun stop() {
                    onStop()

                    processHandle.stop()
                }
            },
        )
    }

}

abstract class AbstractSchedule : Program<Nothing?> {
    @ProcedureFunction
    final override fun execute(): Pair<Nothing?, ProcessHandle> = Pair(null, start())

    abstract fun start(): ProcessHandle
}

fun <E> Cell<E>.forEachInvoke(
    @ProcedureFunction action: (E) -> Unit,
): Schedule {
    action(currentValue)

    return newValues.forEachInvoke(action)
}

fun <E> EventStream<E>.forEachInvoke(
    @ProcedureFunction action: (E) -> Unit,
): Schedule = object : AbstractSchedule() {
    override fun start(): ProcessHandle {
        val subscription = listen(
            object : Listener<E> {
                override fun handle(event: E) {
                    action(event)
                }
            },
        )

        return object : ProcessHandle {
            override fun stop() {
                subscription.cancel()
            }
        }
    }
}

var nextInvokeEachId = 0

fun <E, R> EventStream<E>.invokeEach(
    @ProcedureFunction transform: (E) -> R,
): Program<EventStream<R>> {
    val invokeEachId = ++nextInvokeEachId

    return object : Program<EventStream<R>> {
        override fun execute(): Pair<EventStream<R>, ProcessHandle> {
            val eventEmitter = EventEmitter<R>()

            val subscription = this@invokeEach.listen(
                object : Listener<E> {
                    override fun handle(event: E) {
                        eventEmitter.emit(transform(event))
                    }
                },
            )

            val processHandle = object : ProcessHandle {
                override fun stop() {
                    subscription.cancel()
                }
            }

            return Pair(eventEmitter, processHandle)
        }
    }
}

fun <V, R> Cell<V>.executeCurrentOf(
    transform: (V) -> Program<R>,
): Program<Cell<R>> = map(transform).executeCurrent()

fun <V> Cell<Program<V>>.executeCurrent(): Program<Cell<V>> {
    return object : Program<Cell<V>> {
        override fun execute(): Pair<Cell<V>, ProcessHandle> {
            val initialProgram = currentValue

            val (initialValue, initialProcessHandle) = initialProgram.execute()

            var currentProcessHandle = initialProcessHandle

            val (newValues, updatingProcessHandle) = newValues.invokeEach { newProgram ->
                currentProcessHandle.stop()

                val (newValue, newProcessHandle) = newProgram.execute()

                currentProcessHandle = newProcessHandle

                newValue
            }.execute()

            return Pair(
                newValues.hold(initialValue),
                object : ProcessHandle {
                    override fun stop() {
                        updatingProcessHandle.stop()
                        currentProcessHandle.stop()
                    }
                },
            )
        }
    }
}

private val finalizationRegistry = PlatformFinalizationRegistry()

fun <A> Program<A>.executeBound(
    target: Any,
): A {
    val (result, processHandle) = execute()

    finalizationRegistry.register(
        target = target,
    ) {
        processHandle.stop()
    }

    return result
}
