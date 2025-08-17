package dev.toolkt.reactive.cell

import dev.toolkt.reactive.cell.setExternally
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.createExternally
import dev.toolkt.reactive.event_stream.emitExternally
import dev.toolkt.reactive.event_stream.forEach
import dev.toolkt.reactive.managed_io.activate
import dev.toolkt.reactive.managed_io.endExternally
import dev.toolkt.reactive.managed_io.jumpStartExternally
import kotlin.test.Test
import kotlin.test.assertEquals

class CellActivateTests {
    @Test
    fun testActivate() {
        val eventEmitter1 = EventEmitter.createExternally<Int>()

        val eventEmitter2 = EventEmitter.createExternally<Char>()

        val memoryMutableCell1 = MutableCell.createExternally(initialValue = 0)

        val sourceTrigger1 = eventEmitter1.forEach {
            memoryMutableCell1.set(it)
        }

        val memoryMutableCell2 = MutableCell.createExternally(initialValue = 'a')

        val sourceTrigger2 = eventEmitter2.forEach {
            memoryMutableCell2.set(it)
        }

        val effectMutableCell = MutableCell.createExternally(initialValue = sourceTrigger1)

        // Create the effect
        val activateEffect = effectMutableCell.activate()

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

        // Start the activation effect
        val handle = activateEffect.jumpStartExternally()

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
        effectMutableCell.setExternally(sourceTrigger2)

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
