package managers

import EditorApp
import models.Line

/**
 * The only instance that writes things (i.e., text, cursor) in the terminal.
 */
object RendererManager {
    private val writer = EditorApp.Terminal.writer

    /** The number of rows at the top bar */
    @Suppress("ktlint:standard:property-naming")
    val TOP_ROWS = 2

    /**
     * Renders the top bar of the editor.
     *
     * Don't forget to update [TOP_ROWS] when you make changes in this method
     * to increase/decrease the number of rows.
     */
    internal fun renderTopBar() {
        with(writer) {
            print("\u001b[2J") // Clear screen
            print("\u001b[0;0H") // Move cursor to top left
            print("Welcome to Kurumi's editor!\r\n")
            print("----------------------\r\n")
            flush()
        }
    }

    /**
     * Reflects the latest cursor's position in the terminal.
     *
     * It DOES NOT update the values of [CursorManager].
     * It simply renders the cursor at the latest position.
     */
    internal fun renderCursor() {
        with(writer) {
            // Move the cursor to the absolute position
            print("\u001b[${CursorManager.row.toInt() + TOP_ROWS};${CursorManager.column.toInt()}H")
            flush()
        }
    }

    /**
     * Re-renders the full screen, including the top bar and text buffer.
     */
    internal fun refreshScreenFully() {
        with(writer) {
            // Render the top bar
            print("\u001b[2J") // Clear screen
            print("\u001b[0;0H") // Move cursor to top left
            print("Welcome to Kurumi's editor!\r\n")
            print("----------------------\r\n")

            // Render the entire tree.
            var line: Line? = LineManager.lineHead
            while (line != null) {
                print(line.text.joinToString("") + "\r\n")
                line = line.next
            }

            renderCursor()

            flush()
        }
    }
}
