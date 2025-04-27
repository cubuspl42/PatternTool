package diy.lingerie.test_utils

import diy.lingerie.algebra.NumericObject

object TestTolerances {
    val defaultTolerance = NumericObject.Tolerance.Absolute(
        absoluteTolerance = 10e-6,
    )
}
