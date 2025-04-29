package diy.lingerie.simple_dom

import org.apache.batik.css.engine.value.RGBColorValue
import org.apache.batik.css.engine.value.Value
import org.w3c.dom.css.CSSPrimitiveValue
import kotlin.math.roundToInt

data class SimpleColor(
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    companion object {
        val black = SimpleColor(0, 0, 0)
        val red = SimpleColor(255, 0, 0)
        val green = SimpleColor(0, 255, 0)
        val blue = SimpleColor(0, 0, 255)
    }

    init {
        require(red in 0..255) { "Red value must be between 0 and 255" }
        require(green in 0..255) { "Green value must be between 0 and 255" }
        require(blue in 0..255) { "Blue value must be between 0 and 255" }
    }

    fun toHexString(): String = String.format("#%02X%02X%02X", red, green, blue)
}

fun RGBColorValue.toSimpleColor(): SimpleColor {
    val red = this.red
    val green = this.green
    val blue = this.blue

    return SimpleColor(
        red = red.floatValue.toInt(),
        green = green.floatValue.toInt(),
        blue = blue.floatValue.toInt(),
    )
}

fun Value.toSimpleColor(): SimpleColor {
    if (primitiveType != CSSPrimitiveValue.CSS_RGBCOLOR) {
        throw IllegalArgumentException("Value is not a color")
    }

    return SimpleColor(
        red = red.floatValue.roundToInt(),
        green = green.floatValue.roundToInt(),
        blue = blue.floatValue.roundToInt(),
    )
}
