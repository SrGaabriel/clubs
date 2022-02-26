package dev.gaabriel.clubs.common.util

public class FailedCommandExecutionException(public val failure: FailureType): RuntimeException()