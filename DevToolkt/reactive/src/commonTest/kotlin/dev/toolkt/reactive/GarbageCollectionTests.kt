package dev.toolkt.reactive

import dev.toolkt.core.async_tests.GarbageCollectionTestSuite
import dev.toolkt.core.platform.test_utils.runTestDefault
import dev.toolkt.reactive.event_stream.EventStreamHoldGarbageCollectionTestGroup
import dev.toolkt.reactive.event_stream.EventStreamMapAtGarbageCollectionTestGroup
import dev.toolkt.reactive.event_stream.EventStreamSingleGarbageCollectionTestGroup
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data object ReactiveGarbageCollectionTestSuite : GarbageCollectionTestSuite() {
    override val timeout: Duration = 10.seconds

    override val groups = listOf(
        EventStreamSingleGarbageCollectionTestGroup,
        EventStreamHoldGarbageCollectionTestGroup,
        EventStreamMapAtGarbageCollectionTestGroup,
    )
}

class GarbageCollectionTests {
    @Test
    fun testGarbageCollection() = runTestDefault(
        timeout = 15.seconds,
    ) {
        ReactiveGarbageCollectionTestSuite.execute()
    }
}
