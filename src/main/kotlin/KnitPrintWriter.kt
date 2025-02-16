import models.CursorColumn
import models.CursorRow
import models.Line
import java.io.PrintWriter

/**
 * Custom PrintWriter with additional cursor and command handling.
 */
class KnitPrintWriter(
    out: PrintWriter,
) : PrintWriter(out) {
    /** The topmost line in text buffer. Always points to the head of linked list.*/
    lateinit var lineHead: Line

    /** Current line that user's modifying.*/
    lateinit var currentLine: Line

    // Cursor's position in the terminal
    var cursorColumn: CursorColumn = CursorColumn(0)
    var cursorRow: CursorRow = CursorRow(0)

    /**
     * Moves the cursor in the terminal.
     */
    internal fun moveCursor(
        deltaCol: CursorColumn,
        deltaRow: CursorRow,
    ) {
        // TODO: This is buggy. Fix.
        val row = cursorRow + deltaRow
        val col = cursorColumn + deltaCol
        this.print("\u001b[$row;${col}H")
        this.flush()
    }

    /**
     * Processes a control character such as `\n`.
     * Updates [currentLine] based on the operation.
     *
     * @return updated line position.
     */
    internal fun command(keyType: ControlCharacterKind) {
        when (keyType) {
            ControlCharacterKind.LineFeed, ControlCharacterKind.CarriageReturn -> {
                // Move one line forward.
                val newLine = Line(arrayListOf(), currentLine, null)
                currentLine.next = newLine
                cursorRow++
                refreshScreenFully()
            }
            ControlCharacterKind.Backspace -> {
                if (cursorColumn >= 1) {
                    // Move cursor left by one column.
                    currentLine.text.removeAt(cursorColumn.value - 1)
                }
            }
        }
    }

    /**
     * Inserts a character in the middle of a line.
     *
     * For example,
     * `insert('D', 1)` in `['A', 'B', 'C']`, with cursor pointing to 'B',
     * would make the buffer `['A', 'D, 'B', 'C']` and keep the cursor position the same (note now it points to 'D').
     */
    internal fun insert(char: Char) {
        currentLine.text.add(cursorColumn.value, char)
        refreshScreenFully()
    }

    private fun refreshScreenFully() {
        with(this) {
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

            flush()
        }
    }
}
