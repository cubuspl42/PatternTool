package dev.toolkt.core.collections

interface MutablePrefixSumIndexedList<E> : MutableStableList<E>, PrefixSumIndexedList<E>

fun <E> mutablePrefixSumIndexedListOf(
    vararg elements: E,
): MutablePrefixSumIndexedList<E> = TODO()
