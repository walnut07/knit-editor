// File: src/test/kotlin/controller/ArrowControllerTest.kt
package controller

import managers.CursorManager
import managers.LineManager
import models.ArrowDirection
import models.CursorColumn
import models.CursorRow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrowControllerTest {
    // The controller under test.
    private lateinit var arrowController: ArrowController

    @BeforeTest
    fun setUp() {
        // Reset the managers before each test.
        CursorManager.row = CursorRow(1)
        CursorManager.column = CursorColumn(1)
        LineManager.reset()

        arrowController = ArrowController()
    }

    // ----- Horizontal Moves -----

    @Test
    fun `move RIGHT within bounds`() {
        // Prepare some text and place cursor at 'A'
        LineManager.currentLine.text.addAll("ABCDE".toList())
        CursorManager.column = CursorColumn(1)

        arrowController.move(ArrowDirection.RIGHT)

        assertEquals(2, CursorManager.column.toInt(), "Cursor should move right to column 2")
    }

    @Test
    fun `illegal move RIGHT at right boundary does nothing`() {
        // Prepare text and place cursor at the end "ABCDE|"
        LineManager.currentLine.text.addAll("ABCDE".toList())
        CursorManager.column = CursorColumn(6)

        arrowController.move(ArrowDirection.RIGHT)

        // Cursor should remain at column 6
        assertEquals(6, CursorManager.column.toInt(), "Cursor should remain at column 6 when moving right at boundary")
    }

    @Test
    fun `move LEFT within bounds`() {
        LineManager.currentLine.text.addAll("ABCDE".toList())
        CursorManager.column = CursorColumn(3)

        arrowController.move(ArrowDirection.LEFT)

        assertEquals(2, CursorManager.column.toInt(), "Cursor should move left to column 2")
    }

    @Test
    fun `illegal move LEFT at left boundary does nothing`() {
        LineManager.currentLine.text.addAll("ABCDE".toList())
        CursorManager.column = CursorColumn(1)

        arrowController.move(ArrowDirection.LEFT)

        assertEquals(1, CursorManager.column.toInt(), "Cursor should remain at column 1 when moving left at boundary")
    }

    // ----- Vertical Moves -----

    @Test
    fun `move DOWN within bounds without column adjustment`() {
        // ABCDE
        // WXYZ
        // (Cursor pointing to "C")
        LineManager.currentLine.text.addAll("ABCDE".toList())
        LineManager.addLine(ArrayList("WXYZ".toList()))
        CursorManager.column = CursorColumn(3)
        CursorManager.row = CursorRow(1)
        LineManager.goToPreviousLine()

        arrowController.move(ArrowDirection.DOWN)

        // Expect cursor to move to second row
        assertEquals(2, CursorManager.row.toInt(), "Cursor should move down to row 2")
        assertEquals(3, CursorManager.column.toInt(), "Cursor column should remain unchanged")
    }

    @Test
    fun `move DOWN adjusts column if next line is shorter`() {
        // ABCDE
        // YZ
        // (Cursor pointing to "E")
        LineManager.currentLine.text.addAll("ABCDE".toList())
        LineManager.addLine(ArrayList("YZ".toList()))
        CursorManager.column = CursorColumn(5)
        CursorManager.row = CursorRow(1)
        LineManager.goToPreviousLine()

        arrowController.move(ArrowDirection.DOWN)

        // Expect row to increment and column to adjust
        assertEquals(2, CursorManager.row.toInt(), "Cursor should move down to row 2")
        assertEquals(2, CursorManager.column.toInt(), "Cursor column should adjust to next line's length")
    }

    @Test
    fun `move UP within bounds without column adjustment`() {
        // ABCDE
        // XYZ
        // (Cursor pointing to "Z")
        LineManager.currentLine.text.addAll("ABCDE".toList())
        LineManager.addLine(ArrayList("XYZ".toList()))
        CursorManager.column = CursorColumn(3)
        CursorManager.row = CursorRow(2)

        arrowController.move(ArrowDirection.UP)

        // Expect cursor to move up
        assertEquals(1, CursorManager.row.toInt(), "Cursor should move up to row 1")
        assertEquals(3, CursorManager.column.toInt(), "Cursor column should remain unchanged")
    }

    @Test
    fun `move UP adjusts column if previous line is shorter`() {
        // AB
        // XYZ
        // (Cursor pointing to "Z")
        LineManager.currentLine.text.addAll("AB".toList())
        LineManager.addLine(ArrayList("XYZ".toList()))
        CursorManager.column = CursorColumn(3)
        CursorManager.row = CursorRow(2)
        arrowController.move(ArrowDirection.UP)

        // Expect row to decrement and column to adjust
        assertEquals(1, CursorManager.row.toInt(), "Cursor should move up to row 1")
        assertEquals(2, CursorManager.column.toInt(), "Cursor column should adjust to previous line's length")
    }

    @Test
    fun `illegal move UP from first line does nothing`() {
        LineManager.currentLine.text.addAll("ABCDE".toList())
        CursorManager.row = CursorRow(1)
        CursorManager.column = CursorColumn(3)

        arrowController.move(ArrowDirection.UP)

        // Cursor should remain at row 1
        assertEquals(1, CursorManager.row.toInt(), "Cursor should not move up from first line")
        assertEquals(3, CursorManager.column.toInt(), "Cursor column should remain unchanged")
    }

    @Test
    fun `illegal move DOWN from last line does nothing`() {
        LineManager.currentLine.text.addAll("ABCDE".toList())
        CursorManager.row = CursorRow(1)
        CursorManager.column = CursorColumn(3)

        arrowController.move(ArrowDirection.DOWN)

        // Cursor should remain at row 1
        assertEquals(1, CursorManager.row.toInt(), "Cursor should not move down from last line")
        assertEquals(3, CursorManager.column.toInt(), "Cursor column should remain unchanged")
    }
}
