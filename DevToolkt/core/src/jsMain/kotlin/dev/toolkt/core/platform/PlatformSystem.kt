package dev.toolkt.core.platform

actual object PlatformSystem {
    private var garbage: Any? = null

    actual fun collectGarbage() {
        garbage = DummyGarbage.build(size = 1024 * 1024) // 1 MiB of garbage
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
