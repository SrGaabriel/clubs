package io.github.srgaabriel.clubs.common.annotation

import kotlin.RequiresOptIn.Level.WARNING

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.PROPERTY)
@RequiresOptIn(message = "This property/feature is marked as delicate and thus must be used with caution.", level = WARNING)
public annotation class ClubsDelicateApi