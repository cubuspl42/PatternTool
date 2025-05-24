package diy.lingerie.frp

interface Listener<in E> {
    fun handle(event: E)
}
