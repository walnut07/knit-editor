fun main() {
    enableRawMode()

    val editor = EditorApp()
    editor.run()

    // disableRawMode()
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
