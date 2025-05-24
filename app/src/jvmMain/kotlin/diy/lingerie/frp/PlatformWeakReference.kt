@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package diy.lingerie.frp

import java.lang.ref.WeakReference

actual class PlatformWeakReference<T : Any> actual constructor(value: T) {
    private val weakReference = WeakReference(value)

    actual fun get(): T? = weakReference.get()
}
