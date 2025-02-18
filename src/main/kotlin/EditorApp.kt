import controller.ArrowController
import controller.CommandController
import controller.TextController
import managers.RendererManager
import models.KeyType
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.io.PrintWriter

class EditorApp {
    object Terminal {
        val terminal: org.jline.terminal.Terminal = TerminalBuilder.builder().system(true).build()
        val writer: PrintWriter = terminal.writer()
        val reader: NonBlockingReader = terminal.reader()
    }

    private val reader: NonBlockingReader = Terminal.reader

    // TODO: Consider using Koin for efficient dependency injection.
    private val arrowController = ArrowController()
    private val commandController = CommandController()
    private val textController = TextController()

    /**
     * Starts and ends the application.
     */
    internal fun run() {
        RendererManager.renderTopBar()

        // Start reading user's input.
        while (true) {
            val key = reader.read()
            if (key == 'q'.code) break // Temporarily set

            val keyType: KeyType = KeyType.Utils.parse(reader, key)

            when (keyType) {
                is KeyType.Arrow -> {
                    arrowController.move(keyType.direction)
                }
                is KeyType.ControlCharacter -> {
                    commandController.command(keyType.controlCharacterKind)
                }
                is KeyType.Text -> textController.insert(keyType.value)
                is KeyType.Unknown -> {
                    Terminal.writer.println("Unknown key type $keyType")
                    Terminal.writer.flush()
                }
            }
        }

        Terminal.terminal.close()
    }
}
