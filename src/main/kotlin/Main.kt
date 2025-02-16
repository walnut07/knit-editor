import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder

fun runCProgram() {
    val process =
        ProcessBuilder("./raw_mode")
            .inheritIO()
            .start()

    val exitCode = process.waitFor()
    println("C program exited with code: $exitCode")
}

fun main() {
    println("Running C program from Kotlin...")
    runCProgram()

    val terminal: Terminal = TerminalBuilder.builder().system(true).build()

    val writer = terminal.writer()
    val reader = terminal.reader()

    fun renderScreen() {
        writer.print("\u001b[2J") // Clears the terminal
        writer.print("\u001b[0;0H") // Moves cursor to top left
        writer.println("Welcome to Kurumi's editor!")
        writer.println("----------------------")
        writer.print("\r")
        writer.flush()
    }

    // Move cursor to row `r` and column `c` (1-indexed)
    fun moveCursor(
        writer: java.io.PrintWriter,
        r: Int,
        c: Int,
    ) {
        writer.print("\u001b[$r;${c}H")
        writer.flush()
    }

    renderScreen()

    val lineHead = Line(arrayListOf(), prev = null, next = null)
    var currentLine = lineHead

    var cursorRow = 0
    var cursorColumn = 0

    while (true) {
        val key = reader.read()

        if (key == 27) { // Escape key detected (for arrow keys)
            val next = reader.read()
            if (next == 91) { // '[' in ANSI escape sequence
                val arrowKey = reader.read()
                when (arrowKey) {
                    65 -> { // Up Arrow
                        if (currentLine.prev != null) {
                            currentLine = currentLine.prev!!
                            cursorColumn = minOf(cursorColumn, currentLine.text.size)
                            cursorRow--
                        }
                        moveCursor(writer, cursorRow, cursorColumn)
                    }
                    66 -> { // Down Arrow
                        if (currentLine.next != null) {
                            currentLine = currentLine.next!!
                        } else {
                            val newLine = Line(arrayListOf(), currentLine, null)
                            currentLine.next = newLine
                            currentLine = newLine
                        }
                        cursorColumn = minOf(cursorColumn, currentLine.text.size)
                        cursorRow++
                        moveCursor(writer, cursorRow, cursorColumn)
                    }
                    67 -> { // Right Arrow
                        if (cursorColumn < currentLine.text.size) {
                            cursorColumn++
                        }
                        moveCursor(writer, cursorRow, cursorColumn)
                    }
                    68 -> { // Left Arrow
                        if (cursorColumn >= 1) {
                            cursorColumn--
                        }
                        moveCursor(writer, cursorRow, cursorColumn)
                    }
                }
                writer.flush()
            }
            continue
        }

        when (key.toChar()) {
            '\n', '\r' -> { // Enter key: Create new line
                val newLine = Line(arrayListOf(), currentLine, null)
                currentLine.next = newLine
                currentLine = newLine
                cursorColumn = 0
                cursorRow++
                refreshScreenFully(writer, lineHead)
            }
            '\b', 127.toChar() -> { // Backspace
                if (cursorColumn > 0) {
                    currentLine.text.removeAt(cursorColumn - 1)
                    cursorColumn--
                }
            }
            'q' -> { // Quit
                writer.println("\nExiting editor...")
                writer.flush()
                break
            }
            else -> { // Normal character input
                if (cursorColumn <= currentLine.text.size) {
                    currentLine.text.add(cursorColumn, key.toChar()) // Insert at cursor position.
                    cursorColumn++
                    refreshLine(writer, currentLine, cursorColumn)
                }
            }
        }
    }

    writer.println("\nFinal document structure:\n$lineHead")
    writer.flush()

    terminal.close()
}

fun refreshLine(
    writer: java.io.PrintWriter,
    currentLine: Line,
    currentY: Int,
) {
    writer.print("\r")
    writer.print(currentLine.text.joinToString(""))
    writer.flush()
}

fun refreshScreenFully(
    writer: java.io.PrintWriter,
    head: Line,
) {
    writer.print("\u001b[2J") // Clear screen
    writer.print("\u001b[0;0H") // Move cursor to top left
    writer.print("Welcome to Kurumi's editor!\r\n")
    writer.print("----------------------\r\n")

    var line: Line? = head
    while (line != null) {
        writer.print(line.text.joinToString("") + "\r\n")
        line = line.next
    }
    writer.flush()
}

// Represents a line in a Doubly-Linked List
class Line(
    var text: ArrayList<Char>,
    var prev: Line?,
    var next: Line?,
) {
    override fun toString(): String = "Line(text=\"${text.joinToString("")}\", next=${next?.toString() ?: "null"})"
}
