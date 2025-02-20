package managers

import models.CursorColumn
import models.CursorRow

/**
 * The only instance that manages the cursor's position.
 */
object CursorManager {
    var row = CursorRow(1) // Topmost row.

    var column = CursorColumn(1) // Leftmost row.

    /**
     * @return true if cursor is at the end of the line.
     */
    internal fun isCursorAtEndOfLine(): Boolean = column - 1 == LineManager.currentLine.text.size

    override fun toString(): String = "CursorManager(row=${row.toInt()}, column=${column.toInt()})"
}
