package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.event_stream.forEach
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.actuate
import dev.toolkt.reactive.effect.endExternally
import dev.toolkt.reactive.effect.startExternally
import kotlin.test.Test
import kotlin.test.assertEquals

class CellActuateTests {
    @Test
    fun testActuate() {
        val eventEmitter1 = EventEmitter.createExternally<Int>()

        val eventEmitter2 = EventEmitter.createExternally<Char>()

        val memoryMutableCell1 = MutableCell.createExternally(initialValue = 0)

        val sourceEffect1 = Effect.pureTriggering(
            result = "E1",
            trigger = eventEmitter1.forEach {
                memoryMutableCell1.set(it)
            },
        )

        val memoryMutableCell2 = MutableCell.createExternally(initialValue = 'a')

        val sourceEffect2 = Effect.pureTriggering(
            result = "E2",
            trigger = eventEmitter2.forEach {
                memoryMutableCell2.set(it)
            },
        )

        val effectMutableCell = MutableCell.createExternally(initialValue = sourceEffect1)

        // Create the effect
        val actuateEffect = effectMutableCell.actuate()

        // Emit some source events
        eventEmitter1.emitExternally(10)
        eventEmitter2.emitExternally('b')

        // Verify that it did not change the memory cells (as the effect hasn't started yet)

        assertEquals(
            expected = 0,
            actual = memoryMutableCell1.sampleExternally(),
        )

        assertEquals(
            expected = 'a',
            actual = memoryMutableCell2.sampleExternally(),
        )

        // Start the actuation effect
        val (resultCell, handle) = actuateEffect.startExternally()

        assertEquals(
            expected = "E1",
            actual = resultCell.sampleExternally(),
        )

        eventEmitter1.emitExternally(20)
        eventEmitter2.emitExternally('c')

        // Verify that only the initial effect is active

        assertEquals(
            expected = 20,
            actual = memoryMutableCell1.sampleExternally(),
        )

        assertEquals(
            expected = 'a',
            actual = memoryMutableCell2.sampleExternally(),
        )

        // Set the second effect as currently active
        effectMutableCell.setExternally(sourceEffect2)

        assertEquals(
            expected = "E2",
            actual = resultCell.sampleExternally(),
        )

        eventEmitter1.emitExternally(30)
        eventEmitter2.emitExternally('d')

        // Verify that only the current effect is active

        assertEquals(
            expected = 20,
            actual = memoryMutableCell1.sampleExternally(),
        )

        assertEquals(
            expected = 'd',
            actual = memoryMutableCell2.sampleExternally(),
        )

        // End the effect
        handle.endExternally()

        eventEmitter1.emitExternally(40)
        eventEmitter2.emitExternally('e')

        // Verify that the memory cells did not change after the effect ended

        assertEquals(
            expected = 20,
            actual = memoryMutableCell1.sampleExternally(),
        )

        assertEquals(
            expected = 'd',
            actual = memoryMutableCell2.sampleExternally(),
        )
    }
}
