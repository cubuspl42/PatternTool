package diy.lingerie

import dev.toolkt.App
import kotlin.test.Test
import kotlin.test.assertTrue

class DevToolktTests {
    @Test
    fun testApp() {
        val app = App()

        assertTrue(app.greeting.startsWith("Hello"))
    }
}
