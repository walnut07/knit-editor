package models

// TODO: Needs refactoring.
// Make an abstract class or interface that the two classes will inherit.
// Remove JvmInline from the two classes, so it's value will be mutable.

/**
 * Cursor's vertical position in the terminal.
 * CursorRow = 0 means the cursor is at the topmost row.
 *
 * This inline class prevents confusion with [models.CursorColumn].
 */
@JvmInline
value class CursorRow(
    val value: Int,
) {
    operator fun plus(other: CursorRow): CursorRow = CursorRow(this.value + other.value)

    operator fun plus(other: Int): CursorRow = CursorRow(this.value + other)

    operator fun minus(other: CursorRow): CursorRow = CursorRow(this.value - other.value)

    operator fun minus(other: Int): CursorRow = CursorRow(this.value - other)

    operator fun inc(): CursorRow = CursorRow(this.value + 1)
}

/**
 * Cursor's horizontal position in the terminal.
 * CursorColumn = 0 means the cursor is at the leftmost column.
 *
 * This inline class prevents confusion with [models.CursorRow].
 */
@JvmInline
value class CursorColumn(
    val value: Int,
) {
    operator fun plus(other: CursorColumn): CursorColumn = CursorColumn(this.value + other.value)

    operator fun plus(other: Int): CursorColumn = CursorColumn(this.value + other)

    operator fun minus(other: CursorColumn): CursorColumn = CursorColumn(this.value - other.value)

    operator fun minus(other: Int): CursorColumn = CursorColumn(this.value - other)

    operator fun compareTo(other: Int): Int = value.compareTo(other)
}
