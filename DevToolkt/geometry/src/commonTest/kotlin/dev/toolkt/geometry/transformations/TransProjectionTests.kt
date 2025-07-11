package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Vector2
import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.geometry.Rectangle
import kotlin.test.Test

class TransProjectionTests {
    val sourceOrigin = Point(12.34, 56.78)

    val sourceWidth = 123.4

    val sourceHeight = 22.3

    val targetOrigin = Point(-32.34, 16.78)

    val targetWidth = 229.4

    val targetHeight = 12.3

    val transformation = TransProjection(
        sourceRectangle = Rectangle(
            origin = sourceOrigin,
            width = sourceWidth,
            height = sourceHeight,
        ), targetRectangle = Rectangle(
            origin = targetOrigin,
            width = targetWidth,
            height = targetHeight,
        )
    )

    @Test
    fun testTransformation() {
        assertEqualsWithTolerance(
            expected = targetOrigin,
            actual = transformation.transform(sourceOrigin),
        )

        assertEqualsWithTolerance(
            expected = Point(
                targetOrigin.x + targetWidth,
                targetOrigin.y + targetHeight,
            ),
            actual = transformation.transform(
                Point(
                    sourceOrigin.x + sourceWidth,
                    sourceOrigin.y + sourceHeight,
                ),
            ),
        )

        val ratio = 0.413

        assertEqualsWithTolerance(
            expected = Point(
                targetOrigin.x + ratio * targetWidth,
                targetOrigin.y + ratio * targetHeight,
            ),
            actual = transformation.transform(
                Point(
                    sourceOrigin.x + ratio * sourceWidth,
                    sourceOrigin.y + ratio * sourceHeight,
                ),
            ),
        )
    }

    @Test
    fun testInverse() {
        val invertedTransformation = transformation.invert()

        assertEqualsWithTolerance(
            expected = Point(
                sourceOrigin.x + sourceWidth,
                sourceOrigin.y + sourceHeight,
            ),
            actual = invertedTransformation.transform(
                Point(
                    targetOrigin.x + targetWidth,
                    targetOrigin.y + targetHeight,
                ),
            ),
        )

        val ratio = 0.786

        assertEqualsWithTolerance(
            expected = Point(
                sourceOrigin.x + ratio * sourceWidth,
                sourceOrigin.y + ratio * sourceHeight,
            ),
            actual = invertedTransformation.transform(
                Point(
                    targetOrigin.x + ratio * targetWidth,
                    targetOrigin.y + ratio * targetHeight,
                ),
            ),
        )
    }

    @Test
    fun testBig() {
        val transProjection = TransProjection(
            sourceRectangle = Rectangle.of(
                xMin = 0.0,
                xMax = 1.0,
                yMin = -1e20,
                yMax = 1e20,
            ),
            targetRectangle = Rectangle(
                origin = Point.origin,
                width = 1024.0,
                height = 768.0,
            ),
        )
    }
}
