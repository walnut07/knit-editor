import models.CursorColumn
import models.CursorRow
import models.Line
import java.io.PrintWriter
import kotlin.properties.Delegates

/**
 * The knit's custom PrintWriter with additional cursor and command handling.
 */
class KnitPrintWriter(
    out: PrintWriter,
) : PrintWriter(out) {
    companion object {
        /** The number of rows at the top bar */
        const val TOP_ROWS = 2
    }

    /** The topmost line in text buffer. Always points to the head of linked list.*/
    lateinit var lineHead: Line

    /** Current line that user's modifying.*/
    lateinit var currentLine: Line

    /** Total number of lines in text buffer. */
    var totalLines by Delegates.notNull<Int>()

    // Cursor's position in the terminal.
    // Initially it is placed at the top left.
    var cursorColumn: CursorColumn = CursorColumn()
    var cursorRow: CursorRow = CursorRow()

    /**
     * Processes an arrow key.
     */
    internal fun arrow(direction: ArrowDirection) {
        if (validateCursorPosition(
                rowDelta = direction.deltaRow,
                columnDelta = direction.deltaCol,
            )
        ) {
            cursorRow += direction.deltaRow
            cursorColumn += direction.deltaCol
            renderCursor()
        }
    }

    /**
     * Processes a control character such as `\n`.
     * It may update [currentLine] and other properties.
     */
    internal fun command(input: ControlCharacterKind) {
        when (input) {
            ControlCharacterKind.LineFeed, ControlCharacterKind.CarriageReturn -> {
                // Create a new line.
                val newLine = Line(arrayListOf(), prev = currentLine, next = null)
                currentLine.next = newLine
                currentLine = newLine

                cursorRow++
                cursorColumn.reset()
                totalLines++
                refreshScreenFully()
            }
            ControlCharacterKind.Backspace, ControlCharacterKind.Delete -> {
                // Delete the current character.
                if (cursorColumn.toInt() - 1 >= 1) {
                    // Move cursor left by one column.
                    currentLine.text.removeAt(cursorColumn.toInt() - 2)
                    cursorColumn -= 1
                    refreshScreenFully()
                }
            }

            ControlCharacterKind.Quit -> TODO()
        }
    }

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
            currentLine.text.add(char)
            cursorColumn += 1
        } else {
            currentLine.text.add(cursorColumn.toInt() - 1, char)
        }

        refreshScreenFully()
    }

    /**
     * Reflect the latest cursor's position in the terminal.
     *
     * It DOES NOT update the values of [cursorRow], [cursorColumn], or [currentLine].
     * It simply renders the cursor at the given position.
     */
    private fun renderCursor() {
        // Move the cursor to the absolute position
        print("\u001b[${cursorRow.toInt() + TOP_ROWS};${cursorColumn.toInt()}H")
        flush()
    }

    private fun isAtEndOfLine(): Boolean = cursorColumn.toInt() - 1 == currentLine.text.size

    /** Re-renders the full screen, including the top bar (i.e., Welcome to ...) and text buffer. **/
    private fun refreshScreenFully() {
        with(this) {
            // Render the top bar
            print("\u001b[2J") // Clear screen
            print("\u001b[0;0H") // Move cursor to top left
            print("Welcome to Kurumi's editor!\r\n")
            print("----------------------\r\n")

            // Render the entire tree.
            var line: Line? = lineHead
            while (line != null) {
                print(line.text.joinToString("") + "\r\n")
                line = line.next
            }

            renderCursor()

            flush()
        }
    }

    /**
     * @return false when cursor is going out of frame.
     */
    private fun validateCursorPosition(
        rowDelta: Int = 0,
        columnDelta: Int = 0,
    ): Boolean {
        val destinationRow = cursorRow.toInt() + rowDelta
        val destinationColumn = cursorColumn.toInt() + columnDelta

        if (destinationRow !in 1..totalLines + 1) return false
        if (destinationColumn !in 1..currentLine.text.size) return false
        return true
    }
}
