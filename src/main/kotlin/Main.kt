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

    // Prepare doubly-linked list.
    writer.lineHead = Line(arrayListOf(), prev = null, next = null) // head node
    writer.currentLine = writer.lineHead
    writer.totalLines = 1

    // Ensure the cursor position is set at the top left.
    assertEquals(1, writer.cursorRow.value)
    assertEquals(1, writer.cursorColumn.value)

    // Start reading user's input.
    while (true) {
        val key = reader.read()
        if (key == 'q'.code) break // Temporarily set

        val keyType: KeyType = KeyType.Utils.parse(reader, key)

        when (keyType) {
            is KeyType.Arrow -> {
                // TODO: Encapsulate these operations in [KnitPrintWriter].
                if (writer.validateCursorPosition(
                        rowDelta = keyType.direction.deltaRow.value,
                        columnDelta = keyType.direction.deltaCol.value,
                    )
                ) {
                    writer.cursorRow += keyType.direction.deltaRow
                    writer.cursorColumn += keyType.direction.deltaCol
                    writer.renderCursor()
                }
            }
            is KeyType.ControlCharacter -> {
                // TODO: Handle more control characters like ^C.
                writer.command(keyType.controlCharacterKind)
            }
            is KeyType.Text -> writer.insert(keyType.value)
            is KeyType.Unknown -> {
                writer.println("Unknown key type $keyType")
                writer.flush()
            }
        }
    }

    writer.println("\r\nFinal document structure:\n${writer.lineHead}")
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
