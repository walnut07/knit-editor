package models

import org.jline.utils.NonBlockingReader

/**
 * Represents a type of user's input key.
 */
internal sealed class KeyType {
    /**
     * An arrow key. i.e., ↑, →, ↓, ←.
     */
    class Arrow(
        val direction: ArrowDirection,
    ) : KeyType()

    /**
     * A control character. i.e., \n.
     */
    class ControlCharacter(
        val controlCharacterKind: ControlCharacterKind,
    ) : KeyType()

    /**
     * A character that user intends to write.
     */
    class Text(
        val value: Char,
    ) : KeyType()

    data object Unknown : KeyType()

    object Utils {
        fun parse(
            reader: NonBlockingReader,
            key: Int,
        ): KeyType =
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
    }
}

internal enum class ArrowDirection(
    val deltaCol: Int,
    val deltaRow: Int,
) {
    UP(deltaCol = 0, deltaRow = -1),
    DOWN(deltaCol = 0, deltaRow = 1),
    LEFT(deltaCol = -1, deltaRow = 0),
    RIGHT(deltaCol = 1, deltaRow = 0),
}

internal enum class ControlCharacterKind {
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
