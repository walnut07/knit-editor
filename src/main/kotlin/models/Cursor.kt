package models

abstract class CursorPosition {
    protected abstract var value: Int

    fun reset() {
        value = 1
    }

    fun toInt(): Int = value

    open operator fun inc(): CursorPosition {
        value += 1
        return this
    }

    operator fun dec(): CursorPosition {
        value -= 1
        return this
    }

    operator fun plusAssign(i: Int) {
        value += i
    }

    operator fun minusAssign(i: Int) {
        value -= i
    }

    operator fun plus(i: Int): Int = value + i

    operator fun minus(i: Int): Int = value - i

    operator fun compareTo(other: Int): Int = value.compareTo(other)
}

/**
 * Cursor's vertical position in the terminal.
 * CursorRow = 1 means the cursor is at the topmost row.
 *
 * The purpose of having this class is to prevent confusion with [models.CursorColumn].
 */
class CursorRow(
    override var value: Int,
) : CursorPosition() {
    override fun inc(): CursorRow {
        super.inc()
        return this
    }
}

/**
 * Cursor's horizontal position in the terminal.
 * CursorColumn = 1 means the cursor is at the leftmost column.
 *
 * The purpose of having this class is to prevent confusion with [models.CursorRow].
 */
class CursorColumn(
    override var value: Int,
) : CursorPosition() {
    override fun inc(): CursorColumn {
        super.inc()
        return this
    }
}
