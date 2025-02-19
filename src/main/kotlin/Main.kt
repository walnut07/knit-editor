fun main() {
    enableRawMode()

    val editor = EditorApp()
    editor.run()

    disableRawMode()
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

/**
 * Disables raw mode by running a C program
 */
private fun disableRawMode() {
    val process = ProcessBuilder("./disable_raw_mode").start()
    process.waitFor()
}
