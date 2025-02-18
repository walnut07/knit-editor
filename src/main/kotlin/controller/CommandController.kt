package controller

import managers.CursorManager
import managers.LineManager
import managers.RendererManager
import models.ControlCharacterKind
import models.CursorColumn

/**
 * Contains logic to process command keys.
 *
 * TODO: Handle more control characters like ^C.
 */
class CommandController {
    /**
     * Processes a control character such as \n.
     */
    internal fun command(input: ControlCharacterKind) {
        when (input) {
            ControlCharacterKind.LineFeed, ControlCharacterKind.CarriageReturn -> lineBreak()
            ControlCharacterKind.Backspace, ControlCharacterKind.Delete -> delete()
            ControlCharacterKind.Quit -> TODO()
        }
    }

    /**
     * Creates a line break.
     */
    private fun lineBreak() {
        val currentLineLength = LineManager.currentLine.text.size

        val textToCarry = arrayListOf<Char>()
        LineManager.currentLine.text
            .subList(CursorManager.column.toInt() - 1, currentLineLength)
            .onEach { char -> textToCarry.add(char) }
            .clear() // Remove text to carry from the current line.

        LineManager.addLine(textToCarry)
        CursorManager.row += 1
        CursorManager.column.reset()

        RendererManager.refreshScreenFully()
    }

    /**
     * Deletes the previous character.
     *
     * Example:
     * Assuming we have:
     * [LineManager.currentLine]: `['h', 'e', 'l', 'l', 'o']`
     *
     * If we call [delete] when cursor points to 'e', it becomes:
     * [LineManager.currentLine]: `['e', 'l', 'l', 'o']`
     */
    private fun delete() {
        if (LineManager.totalLines >= 2 && CursorManager.column.toInt() == 1) {
            // Go to the previous row.
            val columnToGoBack =
                CursorColumn(
                    LineManager.currentLine.prev
                        ?.text
                        ?.size
                        ?.plus(1) ?: 1,
                )
            val textToCarry = LineManager.currentLine.text // Carry current text to the column to go back.
            LineManager.removeLine(textToCarry)

            CursorManager.column = columnToGoBack
            CursorManager.row -= 1
        } else if (CursorManager.column - 1 >= 1) {
            // Move cursor left by one column.
            LineManager.removeChar(CursorManager.column.toInt())
            CursorManager.column -= 1
        }

        RendererManager.refreshScreenFully()
    }
}
