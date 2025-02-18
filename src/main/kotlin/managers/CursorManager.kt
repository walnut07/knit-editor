package managers

import models.CursorColumn
import models.CursorRow

/**
 * The only instance that manages the cursor's position.
 */
object CursorManager {
    var row = CursorRow(1) // Topmost row.

    var column = CursorColumn(1) // Leftmost row.
}
