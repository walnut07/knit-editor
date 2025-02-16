import models.CursorColumn
import models.CursorRow
import models.Line
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import kotlin.test.assertEquals

fun main() {
    enableRawMode()

    val terminal: Terminal = TerminalBuilder.builder().system(true).build()
    terminal.renderTopBar()

    val writer = KnitPrintWriter(terminal.writer())
    val reader = terminal.reader()

    // Initialize nodes.
    writer.lineHead = Line(arrayListOf(), prev = null, next = null)
    writer.currentLine = writer.lineHead

    // Ensure the cursor position is set at the top left.
    assertEquals(0, writer.cursorRow.value)
    assertEquals(0, writer.cursorColumn.value)

    // Start reading user's input.
    while (true) {
        val key = reader.read()
        val keyType: KeyType = KeyType.Utils.parse(reader, key)

        when (keyType) {
            is KeyType.Arrow -> {
                writer.moveCursor(
                    deltaCol = CursorColumn(keyType.direction.deltaCol),
                    deltaRow = CursorRow(keyType.direction.deltaRow),
                )
            }
            is KeyType.ControlCharacter -> {
                writer.command(keyType.controlCharacterKind)
            }
            is KeyType.Text -> writer.insert(keyType.value)
            is KeyType.Unknown -> continue
        }
        // TODO: Handle control character like ^C.
    }

    writer.println("\nFinal document structure:\n${writer.lineHead}")
    writer.flush()

    terminal.close()
}

/**
 * Enables raw mode in terminal, by running a C program that uses Termios.
 */
private fun enableRawMode() {
    val process =
        ProcessBuilder("./raw_mode")
            .inheritIO()
            .start()

    process.waitFor()
}
