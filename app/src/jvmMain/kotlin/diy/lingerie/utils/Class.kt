package diy.lingerie.utils

import java.io.Reader

fun <T> Class<T>.getResourceAsReader(name: String): Reader? = this.getResourceAsStream(name)?.reader()
