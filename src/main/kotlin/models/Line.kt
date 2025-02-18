package models

/** A line in text buffer.
 *
 * This is used as a node in Doubly-Linked List to represent text buffer like:
 *
 * (Head [Line]) ⇄ (1st line [Line]) ⇄ (2nd line [Line])
 */
class Line(
    var text: ArrayList<Char>,
    var prev: Line?,
    var next: Line?,
) {
    override fun toString(): String = "Line(text=\"${text.joinToString("")}\", next=${next?.toString() ?: "null"})"
}
