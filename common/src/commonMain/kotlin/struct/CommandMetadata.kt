package dev.gaabriel.clubs.common.struct

public data class CommandMetadata(
    public val data: MutableMap<String, Any?> = mutableMapOf()
) {
    public infix fun <T> String.pair(value: T) {
        data[this] = value
    }

    @Suppress("unchecked_cast")
    public operator fun <T> get(name: String): T? {
        val mapValue = data[name]
        val typedValue = mapValue as? T
        return when {
            mapValue == null -> null
            typedValue == null -> error("The referenced metadata property type does not match with the stored value type.")
            else -> typedValue
        }
    }
}