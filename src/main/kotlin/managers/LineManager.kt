package managers

import models.Line
import kotlin.properties.Delegates

/**
 * The only instance that manages text buffer.
 *
 * It uses Doubly-Linked List to manage the lines of text.
 */
object LineManager {
    /** The topmost line in text buffer. Always points to the head of linked list.*/
    var lineHead: Line
        private set

    /** Current line that user's modifying.*/
    var currentLine: Line
        private set

    /** Total number of lines in text buffer. */
    var totalLines by Delegates.notNull<Int>()
        private set

    // Prepare the linked list.
    init {
        lineHead = Line(arrayListOf(), prev = null, next = null)
        currentLine = lineHead
        totalLines = 1
    }

    /**
     * Adds a new line at the end of the linked list.
     *
     * @param line a line to add.
     */
    fun addLine(line: Line) {
        currentLine.next = line
        currentLine = line
        totalLines++
    }

    /**
     * Removes a designated character of the [currentLine].
     */
    fun removeChar(index: Int) {
        currentLine.text.removeAt(index - 2)
        CursorManager.column -= 1
    }
}
