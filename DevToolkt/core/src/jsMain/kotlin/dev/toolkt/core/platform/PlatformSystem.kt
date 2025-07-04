package dev.toolkt.core.platform

actual object PlatformSystem {
    private var garbage: Any? = null

    actual fun collectGarbage() {
        // ~1M of garbage objects wasn't enough sometimes on the JS target, let's use a bit more
        garbage = DummyGarbage.build(size = 4_000_000)
    }

    actual fun log(value: Any?) {
        console.log(value)
    }
}

private data class DummyGarbage(
    val number: Int = 1,
) {
    companion object {
        fun build(
            size: Int,
        ): Array<DummyGarbage> = Array(size) { DummyGarbage() }
    }
}
