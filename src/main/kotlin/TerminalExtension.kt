import org.jline.terminal.Terminal

// Renders this at the top:
// Welcome to Kurumi's editor!
// ----------------------
fun Terminal.renderTopBar() {
    with(this.writer()) {
        print("\u001b[2J") // Clear screen
        print("\u001b[0;0H") // Move cursor to top left
        print("Welcome to Kurumi's editor!\r\n")
        print("----------------------\r\n")
        flush()
    }
}
