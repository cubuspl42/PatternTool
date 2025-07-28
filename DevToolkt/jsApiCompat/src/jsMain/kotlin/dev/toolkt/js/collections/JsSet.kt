package dev.toolkt.js.collections

@JsName("Set")
external class JsSet<E> {
    constructor()

    val size: Int

    fun add(value: E): JsSet<E>

    fun delete(value: E): Boolean

    fun has(value: E): Boolean

    fun clear()

    fun forEach(callback: (E) -> Unit)
}
