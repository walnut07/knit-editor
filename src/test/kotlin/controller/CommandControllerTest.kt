// File: src/test/kotlin/controller/CommandControllerTest.kt
package controller

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import managers.CursorManager
import managers.LineManager
import managers.RendererManager
import models.ControlCharacterKind
import models.CursorColumn
import models.CursorRow
import models.Line
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CommandControllerTest {
    // Local initial test objects.
    private lateinit var testRow: CursorRow
    private lateinit var testColumn: CursorColumn

    // We'll use these mutable variables to capture updates to CursorManager.
    private lateinit var cursorRow: CursorRow
    private lateinit var cursorColumn: CursorColumn

    // Simulated text buffer.
    private lateinit var linesList: MutableList<Line>

    // The active line.
    private lateinit var currentLine: Line

    // The controller under test.
    private val commandController = CommandController()

    @BeforeTest
    fun setUp() {
        // Initialize our test cursor objects.
        testRow = CursorRow(1)
        testColumn = CursorColumn(1)
        // Set our mutable variables to the initial objects.
        cursorRow = testRow
        cursorColumn = testColumn

        // Initialize a default single-line text buffer ("ABCDE").
        currentLine = Line("ABCDE".toCharArray().toCollection(ArrayList()), prev = null, next = null)
        linesList = mutableListOf(currentLine)

        mockkObject(CursorManager)
        mockkObject(LineManager)
        mockkObject(RendererManager)

        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.row = any() } answers { cursorRow = it.invocation.args[0] as CursorRow }
        every { CursorManager.column } answers { cursorColumn }
        every { CursorManager.column = any() } answers { cursorColumn = it.invocation.args[0] as CursorColumn }

        // Ensure it doesn't render anything.
        every { RendererManager.refreshScreenFully() } returns Unit

        every { LineManager.currentLine } answers { currentLine }
        every { LineManager.totalLines } answers { linesList.size }
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    // ----- Line Break Tests -----

    @Test
    fun `lineBreak splits the line in the middle`() {
        // Prepare a line "HelloWorld" and set cursor at column 6 (after "Hello").
        val text = "HelloWorld".toCharArray().toCollection(ArrayList())
        currentLine = Line(text, prev = null, next = null)
        linesList.clear()
        linesList.add(currentLine)
        // Reset our mutable cursor values.
        testRow = CursorRow(1)
        testColumn = CursorColumn(6)
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Stub LineManager.addLine() to simulate adding a new line.
        every { LineManager.addLine(any()) } answers {
            val carriedText = it.invocation.args[0] as ArrayList<Char>
            val newLine = Line(carriedText, prev = currentLine, next = null)
            currentLine.next = newLine
            currentLine = newLine
            linesList.add(newLine)
        }

        // Act
        commandController.command(ControlCharacterKind.LineFeed)

        // Verify
        assertEquals("Hello", linesList[0].text.joinToString(""), "First line should be 'Hello'")
        assertEquals("World", linesList[1].text.joinToString(""), "Second line should be 'World'")
        assertEquals(2, cursorRow.toInt(), "Row should increment to 2")
        assertEquals(1, cursorColumn.toInt(), "Column should reset to 1")
    }

    @Test
    fun `lineBreak at beginning moves entire text to new line`() {
        // Prepare a line "Hello" with cursor at position 1.
        val text = "Hello".toCharArray().toCollection(ArrayList())
        currentLine = Line(text, prev = null, next = null)
        linesList.clear()
        linesList.add(currentLine)
        testRow = CursorRow(1)
        testColumn = CursorColumn(1)
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Stub addLine().
        every { LineManager.addLine(any()) } answers {
            val carriedText = it.invocation.args[0] as ArrayList<Char>
            val newLine = Line(carriedText, prev = currentLine, next = null)
            currentLine.next = newLine
            currentLine = newLine
            linesList.add(newLine)
        }

        // Act
        commandController.command(ControlCharacterKind.CarriageReturn)

        // Verify
        assertEquals("", linesList[0].text.joinToString(""), "First line should be empty")
        assertEquals("Hello", linesList[1].text.joinToString(""), "Second line should contain 'Hello'")
        assertEquals(2, cursorRow.toInt(), "Row should be 2")
        assertEquals(1, cursorColumn.toInt(), "Column should reset to 1")
    }

    @Test
    fun `lineBreak at end creates an empty new line`() {
        // Prepare a line "Hello" with cursor at the end (position 6).
        val text = "Hello".toCharArray().toCollection(ArrayList())
        currentLine = Line(text, prev = null, next = null)
        linesList.clear()
        linesList.add(currentLine)
        testRow = CursorRow(1)
        testColumn = CursorColumn(6)
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Stub addLine().
        every { LineManager.addLine(any()) } answers {
            val carriedText = it.invocation.args[0] as ArrayList<Char>
            val newLine = Line(carriedText, prev = currentLine, next = null)
            currentLine.next = newLine
            currentLine = newLine
            linesList.add(newLine)
        }

        // Act.
        commandController.command(ControlCharacterKind.LineFeed)

        // Verify.
        assertEquals("Hello", linesList[0].text.joinToString(""), "Original line remains unchanged")
        assertEquals("", linesList[1].text.joinToString(""), "New line should be empty")
        assertEquals(2, cursorRow.toInt(), "Row should increment to 2")
        assertEquals(1, cursorColumn.toInt(), "Column resets to 1")
    }

    // ----- Delete Tests -----

    @Test
    fun `delete removes previous character when cursor is not at beginning`() {
        // Setup a single line "hello".
        val text = "hello".toCharArray().toCollection(ArrayList())
        currentLine = Line(text, prev = null, next = null)
        linesList.clear()
        linesList.add(currentLine)
        testRow = CursorRow(1)
        testColumn = CursorColumn(2)
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Stub removeChar() to simulate deletion.
        every { LineManager.removeChar(any()) } answers {
            val index = it.invocation.args[0] as Int
            currentLine.text.removeAt(index - 2)
        }

        // Act.
        commandController.command(ControlCharacterKind.Backspace)

        // Verify.
        assertEquals("ello", currentLine.text.joinToString(""), "Character should be removed")
        assertEquals(1, cursorColumn.toInt(), "Column should decrease")
    }

    @Test
    fun `delete does nothing if at beginning on single-line document`() {
        // Setup a single line "hello".
        val text = "hello".toCharArray().toCollection(ArrayList())
        currentLine = Line(text, prev = null, next = null)
        linesList.clear()
        linesList.add(currentLine)
        testRow = CursorRow(1)
        testColumn = CursorColumn(1)
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Act.
        commandController.command(ControlCharacterKind.Delete)

        // Verify.
        assertEquals("hello", currentLine.text.joinToString(""), "No deletion should occur")
        assertEquals(1, cursorColumn.toInt(), "Column remains unchanged")
        assertEquals(1, cursorRow.toInt(), "Row remains unchanged")
    }

    @Test
    fun `delete merges lines when at beginning of second line`() {
        // Setup a two-line document: "Hello" and "World".
        val line1 = Line("Hello".toCharArray().toCollection(ArrayList()), prev = null, next = null)
        val line2 = Line("World".toCharArray().toCollection(ArrayList()), prev = line1, next = null)
        line1.next = line2
        linesList.clear()
        linesList.add(line1)
        linesList.add(line2)
        currentLine = line2

        // Set cursor at beginning of second line (column 1) and row 2.
        testRow = CursorRow(2)
        testColumn = CursorColumn(1)
        cursorRow = testRow
        cursorColumn = testColumn
        every { CursorManager.row } answers { cursorRow }
        every { CursorManager.column } answers { cursorColumn }

        // Stub removeLine() to simulate merging.
        every { LineManager.removeLine(any()) } answers {
            line1.text.addAll(line2.text)
            linesList.remove(line2)
            currentLine = line1
        }

        // Act.
        commandController.command(ControlCharacterKind.Backspace)

        // Verify.
        assertEquals("HelloWorld", line1.text.joinToString(""), "Lines should merge")
        assertEquals(1, cursorRow.toInt(), "Row should decrease")
        assertEquals(6, cursorColumn.toInt(), "Column should be previous line length plus one")
    }
}
