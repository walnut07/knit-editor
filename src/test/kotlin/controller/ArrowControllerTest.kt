// File: src/test/kotlin/controller/ArrowControllerTest.kt
package controller

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import managers.CursorManager
import managers.LineManager
import managers.RendererManager
import models.ArrowDirection
import models.CursorColumn
import models.CursorRow
import models.Line
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrowControllerTest {
    // Test instances.
    private lateinit var testRow: CursorRow
    private lateinit var testColumn: CursorColumn

    // We'll use these mutable variables to capture changes.
    private lateinit var currentCursorRow: CursorRow
    private lateinit var currentCursorColumn: CursorColumn

    private lateinit var currentLine: Line

    @BeforeTest
    fun setUp() {
        // Initialize test cursor objects.
        testRow = CursorRow(1)
        testColumn = CursorColumn(1)
        // We'll use mutable variables to capture updates.
        currentCursorRow = testRow
        currentCursorColumn = testColumn

        // Prepare a default current line
        currentLine = Line("ABCDE".toCharArray().toCollection(ArrayList()), prev = null, next = null)

        mockkObject(CursorManager)
        mockkObject(LineManager)
        mockkObject(RendererManager)

        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.row = any() } answers { currentCursorRow = it.invocation.args[0] as CursorRow }
        every { CursorManager.column } answers { currentCursorColumn }
        every { CursorManager.column = any() } answers { currentCursorColumn = it.invocation.args[0] as CursorColumn }

        every { LineManager.currentLine } answers { currentLine }
        every { LineManager.totalLines } answers { 1 }

        // Ensure rendering does nothing.
        every { RendererManager.renderCursor() } returns Unit
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    // ----- Horizontal Moves -----

    @Test
    fun `move RIGHT within bounds`() {
        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.RIGHT)

        // Expect column to change from 1 to 2.
        assertEquals(2, currentCursorColumn.toInt(), "Cursor should move right to column 2")
        assertEquals(1, currentCursorRow.toInt(), "Row should remain unchanged when moving horizontally")
    }

    @Test
    fun `illegal move RIGHT at right boundary does nothing`() {
        currentCursorRow = CursorRow(1)
        currentCursorColumn = CursorColumn(6)
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }
        every { LineManager.currentLine } answers { currentLine }
        every { LineManager.totalLines } answers { 1 }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.RIGHT)

        assertEquals(6, currentCursorColumn.toInt(), "Cursor should remain at column 6 when moving right at boundary")
        assertEquals(1, currentCursorRow.toInt(), "Row should remain unchanged on illegal horizontal move")
    }

    @Test
    fun `illegal move LEFT at left boundary does nothing`() {
        currentCursorRow = CursorRow(1)
        currentCursorColumn = CursorColumn(1)
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }
        every { LineManager.currentLine } answers { currentLine }
        every { LineManager.totalLines } answers { 1 }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.LEFT)

        assertEquals(1, currentCursorColumn.toInt(), "Cursor should remain at column 1 when moving left at boundary")
        assertEquals(1, currentCursorRow.toInt(), "Row should remain unchanged on illegal horizontal move")
    }

    @Test
    fun `move LEFT within bounds`() {
        currentCursorRow = CursorRow(1)
        currentCursorColumn = CursorColumn(3)
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }
        every { LineManager.currentLine } answers { currentLine }
        every { LineManager.totalLines } answers { 1 }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.LEFT)

        assertEquals(2, currentCursorColumn.toInt(), "Cursor should move left to column 2")
        assertEquals(1, currentCursorRow.toInt(), "Row should remain unchanged when moving horizontally")
    }

    // ----- Vertical Moves -----

    @Test
    fun `move DOWN within bounds without column adjustment`() {
        // Set up a two-line document.
        val line1 = Line("ABCDE".toCharArray().toCollection(ArrayList()), prev = null, next = null)
        val line2 = Line("WXYZ".toCharArray().toCollection(ArrayList()), prev = line1, next = null)
        line1.next = line2

        every { LineManager.currentLine } answers { line1 }
        every { LineManager.totalLines } answers { 2 }
        every { LineManager.goToNextLine() } returns Unit

        // Set cursor at row=1, column=3, pointing to "C".
        currentCursorRow = CursorRow(1)
        currentCursorColumn = CursorColumn(3)
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.DOWN)

        // Expect row becomes 2 and column remains 3.
        assertEquals(2, currentCursorRow.toInt(), "Cursor should move down to row 2")
        assertEquals(3, currentCursorColumn.toInt(), "Cursor column should remain unchanged when next line is long enough")
    }

    @Test
    fun `move DOWN adjusts column if next line is shorter`() {
        // Set up a two-line document.
        val line1 = Line("ABCDE".toCharArray().toCollection(ArrayList()), prev = null, next = null)
        val line2 = Line("XY".toCharArray().toCollection(ArrayList()), prev = line1, next = null)
        line1.next = line2

        every { LineManager.currentLine } answers { line1 }
        every { LineManager.totalLines } answers { 2 }
        every { LineManager.goToNextLine() } returns Unit

        // Set cursor at row=1, column=4 (which is greater than line2's length of 2).
        currentCursorRow = CursorRow(1)
        currentCursorColumn = CursorColumn(4) // Points to E
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.DOWN)

        // Expect row becomes 2 and column adjusts to 2.
        assertEquals(2, currentCursorRow.toInt(), "Cursor should move down to row 2")
        assertEquals(2, currentCursorColumn.toInt(), "Cursor column should adjust to next line's length")
    }

    @Test
    fun `move UP within bounds without column adjustment`() {
        // Set up a two-line document.
        val line1 = Line("ABCDE".toCharArray().toCollection(ArrayList()), prev = null, next = null)
        val line2 = Line("WXYZ".toCharArray().toCollection(ArrayList()), prev = line1, next = null)
        line1.next = line2

        // For UP moves, currentLine is line2.
        every { LineManager.currentLine } answers { line2 }
        every { LineManager.totalLines } answers { 2 }
        every { LineManager.goToPreviousLine() } returns Unit

        // Set cursor at row=2, column=3.
        currentCursorRow = CursorRow(2)
        currentCursorColumn = CursorColumn(3) // Y
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.UP)

        // Expect row becomes 1 and column remains 3.
        assertEquals(1, currentCursorRow.toInt(), "Cursor should move up to row 1")
        assertEquals(3, currentCursorColumn.toInt(), "Cursor column should remain unchanged when previous line is long enough")
    }

    @Test
    fun `move UP adjusts column if previous line is shorter`() {
        // Set up a two-line document.
        val line1 = Line("AB".toCharArray().toCollection(ArrayList()), prev = null, next = null)
        val line2 = Line("WXYZ".toCharArray().toCollection(ArrayList()), prev = line1, next = null)
        line1.next = line2

        every { LineManager.currentLine } answers { line2 }
        every { LineManager.totalLines } answers { 2 }
        every { LineManager.goToPreviousLine() } returns Unit

        // Set cursor at row=2, column=4 (greater than line1's length of 2).
        currentCursorRow = CursorRow(2)
        currentCursorColumn = CursorColumn(4) // Z
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.UP)

        // Expect row becomes 1 and column adjusts to 2.
        assertEquals(1, currentCursorRow.toInt(), "Cursor should move up to row 1")
        assertEquals(2, currentCursorColumn.toInt(), "Cursor column should adjust to previous line's length")
    }

    @Test
    fun `illegal move UP from first line does nothing`() {
        // With a single-line document, moving UP is illegal.
        currentCursorRow = CursorRow(1)
        currentCursorColumn = CursorColumn(3)
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }
        every { LineManager.currentLine } answers { currentLine }
        every { LineManager.totalLines } answers { 1 }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.UP)

        assertEquals(1, currentCursorRow.toInt(), "Cursor should not move up from first line")
        assertEquals(3, currentCursorColumn.toInt(), "Cursor column should remain unchanged on illegal move")
    }

    @Test
    fun `illegal move DOWN from last line does nothing`() {
        // With a single-line document, moving DOWN is illegal.
        currentCursorRow = CursorRow(1)
        currentCursorColumn = CursorColumn(3)
        every { CursorManager.row } answers { currentCursorRow }
        every { CursorManager.column } answers { currentCursorColumn }
        every { LineManager.currentLine } answers { currentLine }
        every { LineManager.totalLines } answers { 1 }

        val arrowController = ArrowController()
        arrowController.move(ArrowDirection.DOWN)

        assertEquals(1, currentCursorRow.toInt(), "Cursor should not move down from last line")
        assertEquals(3, currentCursorColumn.toInt(), "Cursor column should remain unchanged on illegal move")
    }
}
