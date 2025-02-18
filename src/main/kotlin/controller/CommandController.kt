package controller

import managers.CursorManager
import managers.LineManager
import managers.RendererManager
import models.ControlCharacterKind
import models.Line

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
            ControlCharacterKind.LineFeed, ControlCharacterKind.CarriageReturn -> {
                LineManager.addLine(Line(arrayListOf(), prev = LineManager.currentLine, next = null))
                CursorManager.row += 1
                CursorManager.column.reset()
                RendererManager.refreshScreenFully()
            }
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
}
