package org.objectscape.ce.backend.util

fun <T> List<T>.last(): T = this.get(this.size)

inline fun <T> Iterable<T>.forEachIsLast(action: (T, isLast: Boolean) -> Unit): Unit {
    var i = 0;
    var it = iterator()
    while(it.hasNext()) {
        action(it.next(), !it.hasNext())
    }
}
