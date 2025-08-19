package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.holdExternally
import dev.toolkt.reactive.cell.sampleExternally
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EventStreamSparkTests {
    private data class Setup(
        val sampledLastId: Int,
        val id: Int,
        val sparkStream: EventStream<Char>,
        val sparkCell: Cell<String>,
    )

    @Test
    fun testSpark() {
        val idEmitter = EventEmitter.createExternally<Int>()

        val lastId: Cell<Int> = idEmitter.holdExternally(initialValue = 0)

        val setupStream = idEmitter.mapAt { id ->
            // Create a spark
            val sparkStream = EventStream.spark('!')

            Setup(
                sampledLastId = lastId.sample(),
                id = id,
                sparkStream = sparkStream,
                // As observing a spark from outside is quite tricky, let's define a cell that holds the spark's value
                // as a proxy
                sparkCell = sparkStream.mapAt {
                    // Sample the external cell, proving that the spark event happened _after_ the event that caused the spark
                    // (but before any future events)
                    "$it:${lastId.sample()}"
                }.hold(""),
            )
        }

        val lastSetup = setupStream.holdExternally(null)

        // Emit the first id
        idEmitter.emitExternally(1)

        val firstSetup = assertNotNull(
            actual = lastSetup.sampleExternally(),
        )

        // Emit another id, to confirm that the spark event happened between the first ID and the second ID events
        // (it's difficult to imagine how could it happen _later_, but let's verify this anyway)
        idEmitter.emitExternally(2)

        assertEquals(
            expected = 0,
            actual = firstSetup.sampledLastId,
        )

        assertEquals(
            expected = 1,
            actual = firstSetup.id,
        )

        assertEquals(
            expected = "!:1",
            actual = firstSetup.sparkCell.sampleExternally(),
        )
    }
}
