package diy.lingerie

import dev.toolkt.Core
import kotlin.test.Test
import kotlin.test.assertTrue

class DevToolktTests {
    @Test
    fun testApp() {
        val core = Core()

        assertTrue(core.greeting.startsWith("Hello"))
    }
}
