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
            ControlCharacterKind.Backspace, ControlCharacterKind.Delete -> {
                // Delete the current character.
                if (CursorManager.column - 1 >= 1) {
                    // Move cursor left by one column.
                    LineManager.removeChar(CursorManager.column.toInt())
                    RendererManager.refreshScreenFully()
                }
            }

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
        CursorManager.column = CursorColumn(textToCarry.size + 1)

        RendererManager.refreshScreenFully()
    }
}
