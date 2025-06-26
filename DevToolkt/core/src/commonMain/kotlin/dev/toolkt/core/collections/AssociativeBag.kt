package dev.toolkt.core.collections

/**
 * An associative bag, i.e. an associative collection which that allows a key
 * to correspond to multiple values.
 */
interface AssociativeBag<K, out V> : Bag<Map.Entry<K, V>>, AssociativeCollection<K, V>

/**
 * Checks whether the bag contains at least one mapping for the specified value.
 */
fun <K, V> AssociativeBag<K, V>.containsValue(
    value: @UnsafeVariance V,
): Boolean = this.any { entry ->
    entry.value == value
}
