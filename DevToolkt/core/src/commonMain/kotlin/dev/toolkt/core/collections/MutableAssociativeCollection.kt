package dev.toolkt.core.collections

/**
 * A generic collection of associative entries that supports adding and removing entries.
 *
 * @param K the type of collection keys
 * @param V the type of collection values
 */
interface MutableAssociativeCollection<K, V> : MutableCollection<Map.Entry<K, V>> {
    /**
     * Adds the specified entry to the collection.
     *
     * @return `true` if the entry has been added, `false` if the entry wasn't added because it collided with another
     * entry
     */
    override fun add(element: Map.Entry<K, V>): Boolean

    /**
     * Adds all the entries of the specified collection to this collection.
     *
     * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
     */
    override fun addAll(elements: Collection<Map.Entry<K, V>>): Boolean
}
