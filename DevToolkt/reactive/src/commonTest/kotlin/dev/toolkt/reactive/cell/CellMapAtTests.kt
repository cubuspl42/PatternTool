package dev.toolkt.reactive.cell

import dev.toolkt.core.platform.PlatformSystem
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.effect.Actions
import dev.toolkt.reactive.test_utils.EventStreamVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CellMapAtTests {
    @Test
    fun testMapAt_listened() = runTestDefault {
        val sourceMutableCell = MutableCell.createExternally(initialValue = 0)

        val sampledMutableCell = MutableCell.createExternally(initialValue = 'A')

        val mappedCellWeakRef = Actions.external {
            sourceMutableCell.mapAt { "$it:${sampledMutableCell.sample()}" }
        }.let { mappedCell ->
            assertTrue(
                actual = sourceMutableCell.hasListeners,
            )

            // Change the sampled cell value (which shouldn't be picked up)
            sampledMutableCell.setExternally('B')

            // Verify the initial value before we start listening, checking that
            // it is based on the value of tha sampled cell from the time the
            // mapping started
            assertEquals(
                expected = "0:A",
                actual = mappedCell.sampleExternally(),
            )

            // Start listening to the cell's new values
            val newValuesVerifier = EventStreamVerifier.setup(
                eventStream = mappedCell.newValues,
            )

            // Change the sampled cell value again (this should be picked up)
            sampledMutableCell.setExternally('C')

            // Change the source cell value
            sourceMutableCell.setExternally(1)

            // Change the sampled cell value again (which shouldn't be picked up)
            sampledMutableCell.setExternally('D')

            // Verify that the new value of the mapped cell reflects the new value
            // of the source cell and the value of the sample cell at the time
            // of mapping

            assertEquals(
                expected = listOf("1:C"),
                actual = newValuesVerifier.removeReceivedEvents(),
            )

            assertEquals(
                expected = "1:C",
                actual = mappedCell.sampleExternally(),
            )

            // Cancel the subscription to the new values stream
            newValuesVerifier.cancel()

            // Keep only a weak reference to the mapped cell
            PlatformWeakReference(mappedCell)
        }

        // Force the garbage collection
        PlatformSystem.collectGarbageForced()

        // Verify that the mapped cell allowed itself to be collected
        assertNull(
            actual = mappedCellWeakRef.get(),
        )

        // Verify that the mapped cell unsubscribed from the source stream
        // as a part of the cleanup process
        assertFalse(
            actual = sourceMutableCell.hasListeners,
        )
    }

    @Test
    fun testMapAt_unlistened() = runTestDefault {
        val sourceMutableCell = MutableCell.createExternally(initialValue = 0)

        val sampledMutableCell = MutableCell.createExternally(initialValue = 'X')

        val mappedCellWeakRef = Actions.external {
            sourceMutableCell.mapAt { "$it:${sampledMutableCell.sample()}" }
        }.let { mappedCell ->
            assertTrue(
                actual = sourceMutableCell.hasListeners,
            )

            // Change the sampled cell value (which shouldn't be picked up)
            sampledMutableCell.setExternally('Y')

            // Verify the initial value before we start listening, checking that
            // it is based on the value of tha sampled cell from the time the
            // mapping started
            assertEquals(
                expected = "0:X",
                actual = mappedCell.sampleExternally(),
            )

            // Change the sampled cell value again (this should be picked up)
            sampledMutableCell.setExternally('Z')

            // Change the source cell value
            sourceMutableCell.setExternally(1)

            // Change the sampled cell value again (which shouldn't be picked up)
            sampledMutableCell.setExternally('W')

            // Verify that the new value of the mapped cell reflects the new value
            // of the source cell and the value of the sample cell at the time
            // of mapping

            assertEquals(
                expected = "1:Z",
                actual = mappedCell.sampleExternally(),
            )

            // Keep only a weak reference to the mapped cell
            PlatformWeakReference(mappedCell)
        }

        // Force the garbage collection
        PlatformSystem.collectGarbageForced()

        // Verify that the mapped cell allowed itself to be collected
        assertNull(
            actual = mappedCellWeakRef.get(),
        )

        // Verify that the mapped cell unsubscribed from the source stream
        // as a part of the cleanup process
        assertFalse(
            actual = sourceMutableCell.hasListeners,
        )
    }
}
