package diy.lingerie.reactive

interface Listener<in E> {
    fun handle(event: E)
}
