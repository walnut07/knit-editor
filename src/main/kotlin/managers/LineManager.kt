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
     * @param text text of the line to add.
     */
    internal fun addLine(text: ArrayList<Char>) {
        val lineToAdd = Line(text, prev = currentLine, next = null)
        val next = currentLine.next
        currentLine.next = lineToAdd
        if (next != null) {
            lineToAdd.next = next
        }
        currentLine = lineToAdd
        totalLines++
    }

    /**
     * Removes the current line from the linked list.
     */
    internal fun removeLine(textToCarry: Collection<Char>) {
        if (currentLine === lineHead) {
            require(currentLine.prev == null)

            val temp = currentLine.next // future head
            currentLine.next = null
            currentLine.next?.prev = null
            currentLine = temp ?: Line()
            lineHead = currentLine
        } else {
            require(currentLine.prev != null)

            val temp = currentLine.prev
            currentLine.prev!!.next = currentLine.next
            currentLine.next?.prev = currentLine.prev
            currentLine = temp ?: Line()
        }
        currentLine.text.addAll(textToCarry)
        require(currentLine.text.size >= 1)
        totalLines--
    }

    /**
     * Removes a character at [index] (1-based) from [currentLine]
     */
    internal fun removeChar(index: Int) {
        if (index < 1 || index > currentLine.text.size + 1) return
        currentLine.text.removeAt(index - 2)
    }

    internal fun goToNextLine() {
        if (currentLine.next != null) {
            currentLine = currentLine.next!!
        }
    }

    internal fun goToPreviousLine() {
        if (currentLine.prev != null) {
            currentLine = currentLine.prev!!
        }
    }
}
