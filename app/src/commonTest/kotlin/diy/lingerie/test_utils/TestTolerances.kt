package diy.lingerie.test_utils

import dev.toolkt.core.numeric.NumericObject

object TestTolerances {
    val defaultTolerance = NumericObject.Tolerance.Absolute(
        absoluteTolerance = 10e-6,
    )
}
