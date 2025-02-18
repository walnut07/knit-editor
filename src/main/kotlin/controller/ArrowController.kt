package controller

import managers.CursorManager
import managers.LineManager
import managers.RendererManager
import models.ArrowDirection

/**
 * Contains logic to process arrow keys.
 */
class ArrowController {
    internal fun move(direction: ArrowDirection) {
        if (validate(direction.deltaRow, direction.deltaCol)) {
            CursorManager.row += direction.deltaRow
            CursorManager.column += direction.deltaCol

            RendererManager.renderCursor()
        }
    }

    /**
     * @return false when cursor is going out of frame.
     */
    private fun validate(
        rowDelta: Int = 0,
        columnDelta: Int = 0,
    ): Boolean {
        val destinationRow = CursorManager.row + rowDelta
        val destinationColumn = CursorManager.column + columnDelta

        if (destinationRow !in 1..LineManager.totalLines + 1) return false
        if (destinationColumn !in 1..LineManager.currentLine.text.size) return false
        return true
    }
}
