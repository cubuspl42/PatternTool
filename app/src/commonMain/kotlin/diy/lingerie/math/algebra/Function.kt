package diy.lingerie.math.algebra

/**
 * @param A domain type
 * @param B codomain type
 */
interface Function<in A, out B> {
    fun apply(a: A): B
}
