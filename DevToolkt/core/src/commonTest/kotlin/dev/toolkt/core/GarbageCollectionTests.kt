package dev.toolkt.core

import dev.toolkt.core.async_tests.GarbageCollectionTestSuite
import dev.toolkt.core.platform.PlatformFinalizationRegistryTestGroup
import dev.toolkt.core.platform.test_utils.runTestDefault
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data object CoreGarbageCollectionTestSuite : GarbageCollectionTestSuite() {
    override val timeout: Duration = 10.seconds

    override val groups = listOf(
        PlatformFinalizationRegistryTestGroup,
    )
}

class GarbageCollectionTests {
    @Test
    fun testGarbageCollection() = runTestDefault(
        timeout = 15.seconds,
    ) {
        CoreGarbageCollectionTestSuite.execute()
    }
}
