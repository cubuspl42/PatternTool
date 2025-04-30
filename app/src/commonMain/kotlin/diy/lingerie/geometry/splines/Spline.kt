package diy.lingerie.geometry.splines

import diy.lingerie.geometry.curves.PrimitiveCurve

/**
 * A composite curve, at least positionally-continuous (C0), either open or
 * closed.
 */
interface Spline {
    val links: List<SplineLink>

    val segmentCurves: List<PrimitiveCurve>
}
