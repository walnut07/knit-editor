import org.jline.utils.NonBlockingReader

sealed class KeyType {
    class Arrow(
        val direction: ArrowDirection,
    ) : KeyType()

    class ControlCharacter(
        val controlCharacterKind: ControlCharacterKind,
    ) : KeyType()

    class Text(
        val value: Char,
    ) : KeyType()

    data object Unknown : KeyType()

    object Utils {
        fun parse(
            reader: NonBlockingReader,
            key: Int,
        ): KeyType {
            val a =
                when (key) {
                    27 -> { // Arrow key
                        val next = reader.read()
                        if (next == 91) { // '[' in ANSI escape sequence
                            when (reader.read()) {
                                65 -> Arrow(ArrowDirection.UP)
                                66 -> Arrow(ArrowDirection.DOWN)
                                67 -> Arrow(ArrowDirection.RIGHT)
                                68 -> Arrow(ArrowDirection.LEFT)
                                else -> Unknown
                            }
                        } else {
                            Unknown
                        }
                    }
                    // Control character
                    '\n'.code, '\r'.code, '\b'.code -> getControlCharacterKind(key)?.let { ControlCharacter(it) } ?: Unknown
                    // Normal key
                    else -> Text(key.toChar())
                }
            return a
        }
    }
}

enum class ArrowDirection(
    val deltaCol: Int,
    val deltaRow: Int,
) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
}

enum class ControlCharacterKind {
    LineFeed, // \n
    CarriageReturn, // \r
    Backspace, // \b
}

private fun getControlCharacterKind(input: Int): ControlCharacterKind? =
    when (input) {
        '\n'.code -> ControlCharacterKind.LineFeed
        '\r'.code -> ControlCharacterKind.CarriageReturn
        '\b'.code -> ControlCharacterKind.Backspace
        else -> null
    }
