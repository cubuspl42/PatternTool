package diy.lingerie.simple_dom

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

        val lightGray = SimpleColor(211, 211, 211)
    }

    init {
        require(red in 0..255) { "Red value must be between 0 and 255" }
        require(green in 0..255) { "Green value must be between 0 and 255" }
        require(blue in 0..255) { "Blue value must be between 0 and 255" }
    }
}
