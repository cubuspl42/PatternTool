package diy.lingerie.utils

fun <T : Any> Iterator<T>.nextOrNull(): T? = when {
    hasNext() -> next()
    else -> null
}
