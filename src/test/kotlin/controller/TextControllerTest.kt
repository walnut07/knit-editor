// File: src/test/kotlin/controller/TextControllerTest.kt
package controller

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import managers.CursorManager
import managers.LineManager
import managers.RendererManager
import models.CursorColumn
import models.CursorRow
import models.Line
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TextControllerTest {
    // Initial test objects.
    private lateinit var testRow: CursorRow
    private lateinit var testColumn: CursorColumn

    // Mutable variables to capture updates from CursorManager.
    private lateinit var cursorRow: CursorRow
    private lateinit var cursorColumn: CursorColumn

    // The simulated current line.
    private lateinit var currentLine: Line

    // The controller under test.
    private val textController = TextController()

    @BeforeTest
    fun setUp() {
        // Initialize our test cursor objects.
        testRow = CursorRow(1)
        testColumn = CursorColumn(1)
        // Set our mutable variables to these initial objects.
        cursorRow = testRow
        cursorColumn = testColumn

        // Initialize a default current line
        currentLine = Line()

        mockkObject(CursorManager)
        mockkObject(LineManager)
        mockkObject(RendererManager)

        // Stub CursorManager so that both its getter and setter update our mutable variables.
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.row = any() } answers { cursorRow = it.invocation.args[0] as CursorRow }
        every { CursorManager.column } answers { cursorColumn }
        every { CursorManager.column = any() } answers { cursorColumn = it.invocation.args[0] as CursorColumn }

        // Stub LineManager to return our current line.
        every { LineManager.currentLine } answers { currentLine }

        // Stub RendererManager.refreshScreenFully() to do nothing.
        every { RendererManager.refreshScreenFully() } returns Unit
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `insert appends character when cursor is at end of line`() {
        // Prepare current line as "ABC"
        currentLine.text.clear()
        currentLine.text.addAll("ABC".toCharArray().toCollection(ArrayList()))
        // Set the cursor so that it's at the end: column = length + 1 = 4.
        testRow = CursorRow(1)
        testColumn = CursorColumn(4)
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Act: Insert 'D'
        textController.insert('D')

        // Verify
        assertEquals("ABCD", currentLine.text.joinToString(""), "Character should be appended at end of line")
        assertEquals(5, cursorColumn.toInt(), "Cursor column should increment to 5")
    }

    @Test
    fun `insert inserts character in middle when cursor is not at the end`() {
        currentLine.text.clear()
        currentLine.text.addAll("ABC".toCharArray().toCollection(ArrayList()))
        // Set the cursor so that it's not at the end: column = 2.
        testRow = CursorRow(1)
        testColumn = CursorColumn(2) // Points to B
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Act: Insert 'D'
        textController.insert('D')

        // Verify
        assertEquals("ADBC", currentLine.text.joinToString(""), "Character should be inserted at the correct position")
        assertEquals(3, cursorColumn.toInt(), "Cursor column should increment to 3")
    }
}
