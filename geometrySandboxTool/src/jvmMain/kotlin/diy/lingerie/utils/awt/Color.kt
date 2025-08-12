package diy.lingerie.utils.awt

import java.awt.Color

fun Color.toHexString(): String {
    val red = this.red
    val green = this.green
    val blue = this.blue
    return String.format("#%02x%02x%02x", red, green, blue)
}
