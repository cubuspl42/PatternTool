@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package diy.lingerie.frp

interface PlatformCleanable {
    fun clean()
}

expect class PlatformFinalizationRegistry {
    constructor()

    fun register(
        target: Any,
        cleanup: () -> Unit,
    ): PlatformCleanable
}
