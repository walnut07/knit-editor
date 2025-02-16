import org.jline.terminal.Terminal

// Renders this at the top:
// Welcome to Kurumi's editor!
// ----------------------
fun Terminal.renderTopBar() {
    val writer = this.writer()
    writer.print("\u001b[2J") // Clears the terminal
    writer.print("\u001b[0;0H") // Moves cursor to top left
    writer.println("Welcome to Kurumi's editor!")
    writer.println("----------------------")
    writer.print("\r")
    writer.flush()
}
