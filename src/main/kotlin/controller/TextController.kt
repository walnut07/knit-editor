package controller

import managers.CursorManager
import managers.CursorManager.isCursorAtEndOfLine
import managers.LineManager
import managers.RendererManager

/**
 * Contains logic to process a character that user intend to write.
 */
class TextController {
    /**
     * Inserts a character at the end of or in the middle of a line.
     */
    internal fun insert(char: Char) {
        if (isCursorAtEndOfLine()) {
            LineManager.currentLine.text.add(char)
        } else {
            LineManager.currentLine.text.add(CursorManager.column - 1, char)
        }
        CursorManager.column += 1
        RendererManager.refreshScreenFully()
    }
}
