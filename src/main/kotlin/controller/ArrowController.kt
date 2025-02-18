package controller

import managers.CursorManager
import managers.LineManager
import managers.RendererManager
import models.ArrowDirection
import models.CursorColumn

/**
 * Contains logic to process arrow keys.
 */
class ArrowController {
    /**
     * Moves the cursor to the given direction.
     */
    internal fun move(direction: ArrowDirection) {
        when (direction) {
            ArrowDirection.UP, ArrowDirection.DOWN -> moveUpOrDown(direction.deltaRow)
            ArrowDirection.LEFT, ArrowDirection.RIGHT -> moveLeftOrRight(direction.deltaCol)
        }
        RendererManager.renderCursor()
    }

    /**
     * Moves the cursor vertically.
     */
    private fun moveUpOrDown(deltaRow: Int) {
        val destinationRow = CursorManager.row + deltaRow

        // 1) Is it within the lines? If not, that's an illegal move.
        if (destinationRow !in 1..LineManager.totalLines) return

        if (deltaRow > 0) {
            // Moving down.
            // 2) Is the next line long enough to place cursor?
            val nextLineLength =
                LineManager.currentLine.next
                    ?.text
                    ?.size ?: 0
            if (nextLineLength < CursorManager.column.toInt()) {
                // If not, place the cursor at the last index of the next line.
                CursorManager.column = CursorColumn(nextLineLength)
            }
            LineManager.goToNextLine()
            CursorManager.row += deltaRow // Move down.
        } else {
            // Moving up.
            // 2) Is the previous line long enough to place cursor?
            val prevLineLength =
                LineManager.currentLine.prev
                    ?.text
                    ?.size ?: 0
            if (prevLineLength < CursorManager.column.toInt()) {
                // If not, place cursor at the last index of the previous line.
                CursorManager.column = CursorColumn(prevLineLength)
            }
            LineManager.goToPreviousLine()
            CursorManager.row += deltaRow // Move up.
        }
    }

    /**
     * Moves the cursor horizontally.
     */
    private fun moveLeftOrRight(deltaCol: Int) {
        val destinationColumn = CursorManager.column + deltaCol

        // Is it within the current line? If not, that's an illegal move.
        if (destinationColumn !in 1..LineManager.currentLine.text.size + 1) return
        // Update cursor.
        CursorManager.column += deltaCol
    }
}
