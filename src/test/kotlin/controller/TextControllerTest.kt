// File: src/test/kotlin/controller/TextControllerTest.kt
package controller

import managers.CursorManager
import managers.LineManager
import models.CursorColumn
import models.CursorRow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TextControllerTest {
    // The controller under test.
    private lateinit var textController: TextController

    @BeforeTest
    fun setUp() {
        // Reset the managers before each test.
        CursorManager.row = CursorRow(1)
        CursorManager.column = CursorColumn(1)
        LineManager.reset()

        textController = TextController()
    }

    @Test
    fun `insert appends character when cursor is at end of line`() {
        // Append some text and set cursor at the end.
        LineManager.currentLine.text.addAll("ABC".toList())
        CursorManager.column = CursorColumn(4)

        // Act: Insert 'D'
        textController.insert('D')

        // Verify:
        assertEquals("ABCD", LineManager.currentLine.text.joinToString(""), "Character should be appended at the end of the line")
        assertEquals(5, CursorManager.column.toInt(), "Cursor column should increment to 5")
    }

    @Test
    fun `insert inserts character in middle when cursor is not at the end`() {
        // Append some text and set cursor in the middle.
        LineManager.currentLine.text.addAll("ABC".toList())
        CursorManager.column = CursorColumn(2)

        // Act: Insert 'D'
        textController.insert('D')

        // Verify:
        assertEquals("ADBC", LineManager.currentLine.text.joinToString(""), "Character should be inserted at the correct position")
        assertEquals(3, CursorManager.column.toInt(), "Cursor column should increment to 3")
    }
}
