import org.jline.utils.NonBlockingReader

/**
 * Contains key types that knit supports.
 */
internal sealed class KeyType {
    /**
     * Represents an arrow key such as ↑, →, ↓, ←.
     */
    class Arrow(
        val direction: ArrowDirection,
    ) : KeyType()

    class ControlCharacter(
        val controlCharacterKind: ControlCharacterKind,
    ) : KeyType()

    /**
     * A character that user intended to write.
     */
    class Text(
        val value: Char,
    ) : KeyType()

    data object Unknown : KeyType()

    object Utils {
        fun parse(
            reader: NonBlockingReader,
            key: Int,
        ): KeyType {
            val a
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
                '\n'.code, '\r'.code, '\b'.code, 127 -> getControlCharacterKind(key)?.let { ControlCharacter(it) } ?: Unknown
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
    UP(deltaCol = 0, deltaRow = -1),
    DOWN(deltaCol = 0, deltaRow = 1),
    LEFT(deltaCol = -1, deltaRow = 0),
    RIGHT(deltaCol = 1, deltaRow = 0),
}

enum class ControlCharacterKind {
    LineFeed, // \n
    CarriageReturn, // \r
    Backspace, // \b
    Delete, // ASCII 127
    Quit, // q
}

private fun getControlCharacterKind(input: Int): ControlCharacterKind? =
    when (input) {
        '\n'.code -> ControlCharacterKind.LineFeed
        '\r'.code -> ControlCharacterKind.CarriageReturn
        '\b'.code -> ControlCharacterKind.Backspace
        127 -> ControlCharacterKind.Delete
        'C'.code -> ControlCharacterKind.Quit
        else -> null
    }
