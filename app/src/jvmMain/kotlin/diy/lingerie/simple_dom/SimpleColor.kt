package diy.lingerie.simple_dom

fun SimpleColor.toHexString(): String = String.format("#%02X%02X%02X", red, green, blue)
