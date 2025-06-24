package dev.toolkt.core.collections

/**
 * Bag is an extremely elastic kind of collection. Bag doesn't impose any meaningful (or even stable) order. It doesn't
 * guarantee any efficient lookup operations. It doesn't require element uniqueness. It doesn't require from its elements
 * a functional [hashCode] or that they implement [Comparable]. For many operations bag doesn't even require from its
 * elements a functional [equals].
 *
 * [Bag] interface has exactly the same methods as [Collection], but (like [Iterable] vs [Sequence]) introduces a
 * semantical distinction. [Collection] is an interface that allows both collections imposing uniqueness and those
 * that don't. [Bag] allows duplicates explicitly. [Bag] is not an abstraction for a family of collections
 * shapes with different guarantees (like [Collection]), but an abstraction for a specific collection shape with few
 * guarantees. This is more prominent in the [MutableBag] interface.
 *
 * [Bag] might appear similar to [List] (both don't guarantee any efficient lookup operations, don't require element
 * uniqueness) but the list has a known semantic order, while the bag doesn't. List might be considered a special type
 * of bag.
 *
 * [Bag] might appear similar to third-party `MultiSet` collections (isn't fundamentally ordered, doesn't require element
 * uniqueness), but bag doesn't provide any efficient element counting, while multiset does. In turn, bag doesn't require
 * [hashCode] or [Comparable] implementation. Multiset might be considered a special type of bag.
 */
interface Bag<E> : Collection<E>
