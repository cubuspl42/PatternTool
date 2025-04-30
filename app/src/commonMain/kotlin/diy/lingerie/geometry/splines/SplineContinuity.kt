package diy.lingerie.geometry.splines

/**
 * Spline continuity is a property of a spline that describes the continuity of
 * the spline at the knots. It is used to determine how the spline behaves
 * at the knots and how the segments are connected. There are multiple forms of
 * spline continuity and this class represents only a small arbitrary subset.
 *
 * This is a "tag class", not intended to have any instances.
 */
sealed interface SplineContinuity {
    /**
     * A positional continuity, also called C0/G0 continuity, means that the
     * spline is continuous at the knots, but the first derivative may not
     * be continuous. This means that the spline may have a corner at the
     * knots, but the segments are connected.
     */
    sealed interface Positional : SplineContinuity

    /**
     * A tangent continuity, also called G1 continuity, means that the
     * spline is continuous at the knots and the first derivative is also
     * continuous. This means that the spline is smooth at the knots and
     * the segments are connected.
     */
    sealed interface Tangent : Positional
}
