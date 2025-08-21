/**
 * Reimplementation of the assertion utils from the `kotlin.test` framework
 */
package dev.toolkt.reactive.system_tests.utils

/**
 * Asserts that the expression is `true` with an optional [message].
 */
fun assertTrue(
    actual: Boolean,
    message: String? = null,
) {
    if (!actual) {
        throw AssertionError(message ?: "Expected value to be true.")
    }
}

/**
 * Asserts that the expression is `false` with an optional [message].
 */
fun assertFalse(
    actual: Boolean,
    message: String? = null,
) {
    if (actual) {
        throw AssertionError(message ?: "Expected value to be false.")
    }
}

/**
 * Asserts that the expression is `null` with an optional [message].
 */
fun assertNull(
    actual: Any?,
    message: String? = null,
) {
    if (actual != null) {
        throw AssertionError(message ?: "Expected value to be null, but was: <$actual>.")
    }
}
