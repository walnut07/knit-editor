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
    assertEquals(1, writer.cursorRow.toInt())
    assertEquals(1, writer.cursorColumn.toInt())

    // Start reading user's input.
    while (true) {
        val key = reader.read()
        if (key == 'q'.code) break // Temporarily set

        val keyType: KeyType = KeyType.Utils.parse(reader, key)

        when (keyType) {
            is KeyType.Arrow -> {
                writer.arrow(keyType.direction)
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
