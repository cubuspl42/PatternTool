package dev.toolkt.core.collections

/**
 * An indexed list of integers providing an additional efficient operation for
 * calculating the prefix sum.
 */
interface PrefixSumIndexedList : IndexedList<Int> {
    /**
     * Calculates the sum of the first [count] elements in the list.
     * Guarantees logarithmic time complexity.
     */
    fun calculatePrefixSum(
        count: Int,
    ): Int
}
