package managers

import models.CursorColumn
import models.CursorRow

/**
 * The only instance that manages the cursor's position.
 */
object CursorManager {
    /** Use [CursorRow]'s methods to manipulate. */
    var row = CursorRow(1) // Topmost row.
        private set

    /** Use [CursorColumn]'s methods to manipulate. */
    var column = CursorColumn(1) // Leftmost row.
        private set
}
