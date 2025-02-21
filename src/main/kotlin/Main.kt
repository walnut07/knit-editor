fun main() {
    enableRawMode()

    val editor = EditorApp()
    editor.run()

    disableRawMode()
}

const val BINARY_PATH = "./script/bin"

/**
 * Enables raw mode in terminal, by running a C program that uses Termios.
 */
private fun enableRawMode() {
    val process =
        ProcessBuilder("$BINARY_PATH/raw_mode")
            .inheritIO()
            .start()

    process.waitFor()
}

/**
 * Disables raw mode by running a C program
 */
private fun disableRawMode() {
    val process = ProcessBuilder("$BINARY_PATH/disable_raw_mode").start()
    process.waitFor()
}
