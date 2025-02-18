package controller

import managers.CursorManager
import managers.LineManager
import managers.RendererManager

/**
 * Contains logic to process a character that user intend to write.
 */
class TextController {
    /**
     * Inserts a character at the end of or in the middle of a line.
     *
     * For example,
     * `insert('D')` in `['A', 'B', 'C']` with cursor pointing to 'B'
     * would make the buffer `['A', 'D, 'B', 'C']` and keep the cursor position the same (note now it points to 'D').
     *
     */
    internal fun insert(char: Char) {
        if (isAtEndOfLine()) {
            LineManager.currentLine.text.add(char)
            CursorManager.column += 1
        } else {
            LineManager.currentLine.text.add(CursorManager.column - 1, char)
        }

        RendererManager.refreshScreenFully()
    }

    private fun isAtEndOfLine(): Boolean = CursorManager.column - 1 == LineManager.currentLine.text.size
}
