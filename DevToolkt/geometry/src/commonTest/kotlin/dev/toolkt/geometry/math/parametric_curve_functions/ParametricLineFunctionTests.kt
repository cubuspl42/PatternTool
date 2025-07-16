package dev.toolkt.geometry.math.parametric_curve_functions

import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.math.algebra.linear.vectors.Vector2
import kotlin.test.Test

class ParametricLineFunctionTests {
    @Test
    fun testFindDerivativeCurve_simple() {
        val parametricLineFunction = ParametricLineFunction.of(
            Vector2(a0 = 205.43067626953143, a1 = 177.99880957031263),
            Vector2(a0 = 181.00170593261697, a1 = -183.43269914245616),
        )

        val derivativeCurve: ParametricPointFunction = parametricLineFunction.findDerivativeCurve()

        assertEqualsWithTolerance(
            expected = ParametricPointFunction(
                p = Vector2(a0 = -24.42897033691446, a1 = -361.4315087127688),
            ),
            actual = derivativeCurve,
        )
    }
}
