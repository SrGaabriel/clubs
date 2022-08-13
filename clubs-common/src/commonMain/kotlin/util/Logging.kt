package dev.gaabriel.clubs.common.util

public interface ClubsLogger {
    public var enabledLogLevels: MutableSet<LogLevel>

    public fun log(level: LogLevel, message: String, exception: Throwable? = null)

    public fun enableLogLevel(level: LogLevel) {
        enabledLogLevels.add(level)
    }

    public fun disableLogLevel(level: LogLevel) {
        enabledLogLevels.remove(level)
    }

    public enum class LogLevel {
        Info,
        Debug,
        Warning,
        Error
    }
}