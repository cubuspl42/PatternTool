package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.test.Test

class ParametricPointFunctionTests {
    @Test
    fun testFindDerivativeCurve_simple() {
        val parametricPointFunction = ParametricPointFunction(
            p = Vector2(a0 = -24.42897033691446, a1 = -361.4315087127688),
        )

        val derivativeCurve: ParametricPointFunction = parametricPointFunction.findDerivativeCurve()

        assertEqualsWithTolerance(
            expected = ParametricPointFunction.Zero,
            actual = derivativeCurve,
        )
    }
}
