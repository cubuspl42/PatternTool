@file:OptIn(ExperimentalContracts::class)

package diy.lingerie.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T> T.alsoApply(block: T.(T) -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.block(this)
    return this
}
