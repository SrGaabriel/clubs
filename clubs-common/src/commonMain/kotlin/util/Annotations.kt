package dev.gaabriel.clubs.common.util

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class ClubsInternalAPI