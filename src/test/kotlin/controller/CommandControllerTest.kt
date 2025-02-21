@file:Suppress("ktlint:standard:no-wildcard-imports")

package controller

import managers.CursorManager
import managers.LineManager
import models.ControlCharacterKind
import models.CursorColumn
import models.CursorRow
import kotlin.test.*

class CommandControllerTest {
    // The controller under test
    private val commandController = CommandController()

    @BeforeTest
    fun setUp() {
        // Reset the editor state before each test
        CursorManager.row = CursorRow(1)
        CursorManager.column = CursorColumn(1)
        LineManager.reset()
    }

    // ----- Line Break Tests -----

    @Test
    fun `lineBreak splits the line in the middle`() {
        // Set up a line "HelloWorld" and set cursor at column 6 (after "Hello")
        LineManager.currentLine.text.addAll("HelloWorld".toList())
        CursorManager.column = CursorColumn(6)

        // Act - Press Enter
        commandController.command(ControlCharacterKind.LineFeed)

        // Verify - Line should split at position 6
        assertEquals("Hello", LineManager.lineHead.text.joinToString(""))
        assertEquals(
            "World",
            LineManager.lineHead.next
                ?.text
                ?.joinToString(""),
        )
        assertEquals(2, CursorManager.row.toInt(), "Row should increment")
        assertEquals(1, CursorManager.column.toInt(), "Column should reset")
    }

    @Test
    fun `lineBreak at beginning moves entire text to new line`() {
        // Set up a line "Hello" and place cursor at beginning
        LineManager.currentLine.text.addAll("Hello".toList())

        // Act - Press Enter
        commandController.command(ControlCharacterKind.LineFeed)

        // Verify - First line should be empty, second line should contain "Hello"
        assertEquals("", LineManager.lineHead.text.joinToString(""))
        assertEquals(
            "Hello",
            LineManager.lineHead.next
                ?.text
                ?.joinToString(""),
        )
        assertEquals(2, CursorManager.row.toInt(), "Row should increment")
        assertEquals(1, CursorManager.column.toInt(), "Column should reset")
    }

    @Test
    fun `lineBreak at end creates an empty new line`() {
        // Set up a line "Hello" and place cursor at end
        LineManager.currentLine.text.addAll("Hello".toList())
        CursorManager.column = CursorColumn(6)

        // Act - Press Enter
        commandController.command(ControlCharacterKind.LineFeed)

        // Verify - First line should remain unchanged, second line should be empty
        assertEquals("Hello", LineManager.lineHead.text.joinToString(""))
        assertEquals(
            "",
            LineManager.lineHead.next
                ?.text
                ?.joinToString(""),
        )
        assertEquals(2, CursorManager.row.toInt(), "Row should increment")
        assertEquals(1, CursorManager.column.toInt(), "Column should reset")
    }

    // ----- Delete Tests -----

    @Test
    fun `delete removes previous character when cursor is not at beginning`() {
        // Set up a line "hello" and place cursor at position 2
        LineManager.currentLine.text.addAll("hello".toList())
        CursorManager.column = CursorColumn(2)

        // Act - Press Backspace
        commandController.command(ControlCharacterKind.Backspace)

        // Verify - "h" should be removed
        assertEquals("ello", LineManager.currentLine.text.joinToString(""))
        assertEquals(1, CursorManager.column.toInt(), "Column should decrease")
    }

    @Test
    fun `delete does nothing if at beginning on single-line document`() {
        // Set up a line "hello" and place cursor at beginning
        LineManager.currentLine.text.addAll("hello".toList())

        // Act - Press Backspace at column 1
        commandController.command(ControlCharacterKind.Backspace)

        // Verify - No change should happen
        assertEquals("hello", LineManager.currentLine.text.joinToString(""))
        assertEquals(1, CursorManager.column.toInt(), "Column remains unchanged")
        assertEquals(1, CursorManager.row.toInt(), "Row remains unchanged")
    }

    @Test
    fun `delete merges lines when at beginning of second line`() {
        // Set up two lines: "Hello" and "World"
        LineManager.currentLine.text.addAll("Hello".toList())
        CursorManager.column = CursorColumn(6)
        commandController.command(ControlCharacterKind.LineFeed) // Press Enter
        LineManager.currentLine.text.addAll("World".toList())
        CursorManager.column = CursorColumn(6)

        // Move cursor to beginning of second line
        CursorManager.row = CursorRow(2)
        CursorManager.column = CursorColumn(1)

        // Act - Press Backspace
        commandController.command(ControlCharacterKind.Backspace)

        // Verify - Second line should be merged with first
        assertEquals("HelloWorld", LineManager.lineHead.text.joinToString(""))
        assertEquals(1, LineManager.totalLines, "Only one line should remain")
        assertEquals(1, CursorManager.row.toInt(), "Cursor should move back to first line")
        assertEquals(6, CursorManager.column.toInt(), "Cursor should be at end of Hello")
    }

    @Test
    fun `delete twice removes consecutive empty lines and moves cursor to previous line`() {
        // Set up a document:
        //   - First line: "Hello"
        //   - Second line: (empty)
        //   - Third line: (empty)
        LineManager.currentLine.text.addAll("Hello".toList())
        CursorManager.column = CursorColumn(6)
        commandController.command(ControlCharacterKind.LineFeed) // Create empty line
        commandController.command(ControlCharacterKind.LineFeed) // Create another empty line

        // Move cursor to third line
        CursorManager.row = CursorRow(3)
        CursorManager.column = CursorColumn(1)

        // Act - Press Delete twice
        commandController.command(ControlCharacterKind.Delete) // Deletes third line
        commandController.command(ControlCharacterKind.Delete) // Deletes second line

        // Verify - Both empty lines should be removed
        assertEquals(1, LineManager.totalLines, "Only 'Hello' should remain")
        assertEquals("Hello", LineManager.lineHead.text.joinToString(""))
        assertEquals(1, CursorManager.row.toInt(), "Cursor should move back to first line")
        assertEquals(6, CursorManager.column.toInt(), "Cursor should be at the end of 'Hello'")
    }
}
