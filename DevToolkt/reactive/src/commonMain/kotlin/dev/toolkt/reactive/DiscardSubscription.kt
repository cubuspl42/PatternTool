package dev.toolkt.reactive

/**
 * Annotation to indicate that a subscription is discarded intentionally, most
 * likely because the caller wills to rely on garbage collection instead.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.EXPRESSION)
annotation class DiscardSubscription
