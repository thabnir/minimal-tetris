import java.awt.Color
import java.util.stream.IntStream.range
import kotlin.streams.toList

class Board {
    val staticGrid: ArrayList<Array<Cell>> = ArrayList()
    val activeGrid: ArrayList<Array<Cell>> = ArrayList()
    val ghostGrid: ArrayList<Array<Cell>> = ArrayList()
    val numCols = 10
    val numRows = 20
    val emptyColor = Color.WHITE

    val nextPieces: ArrayList<Piece> = ArrayList() // 1 more than shows on screen
    var pieceBag: ArrayList<Piece> = ArrayList()

    var holdUsed = false
    var holdPiece: Piece? = null
    var activePiece: Piece
    var ghostPiece: Piece

    var linesCleared = 0

    init {
        for (i in 0 until numRows) {
            staticGrid.add(Array(numCols) { Cell(false, emptyColor) })
            activeGrid.add(Array(numCols) { Cell(false, emptyColor) })
            ghostGrid.add(Array(numCols) { Cell(false, emptyColor) })
        }
        for (i in 0 until 4) {
            nextPieces.add(generatePiece())
        }
        activePiece = LPiece()
        getNextPiece()
        ghostPiece = activePiece.clone(activePiece.direction)
        update()
    }

    fun generateShuffledBag(): ArrayList<Piece> {
        val pieceList = listOf(LPiece(), JPiece(), IPiece(), TPiece(), OPiece(), SPiece(), ZPiece())
        val onetoseven = range(0, 7).toList().shuffled()
        val pieces = ArrayList<Piece>()
        for (num in onetoseven) {
            pieces.add(pieceList[num])
        }
        return pieces
    }

    fun generatePiece(): Piece {
        if (pieceBag.size == 0) {
            pieceBag.addAll(generateShuffledBag())
        }
        val np = pieceBag[0]
        pieceBag.removeAt(0)
        return np
    }

    fun getNextPiece() {
        nextPieces.add(generatePiece())
        activePiece = nextPieces[0]
        nextPieces.removeAt(0)
    }

    fun softDrop(): Boolean {
        return softDrop(activePiece)
    }

    fun softDrop(piece: Piece): Boolean {
        val pieceCoordArray = piece.getCoordinates()
        for (coord in pieceCoordArray) {
            val x = coord % 4 + piece.coords.x
            val y = coord / 4 + piece.coords.y + 1
            if (y < 0 || x < 0 || x > 9) {
                return false
            } else if (y > 19 || staticGrid[y][x].isFilled) {
                if (activePiece == piece) placePiece()
                return false
            }
        }
        piece.moveDown()
        return true
    }

    fun hardDrop() {
        while (softDrop()) {
            // works as intended without anything here
        }
    }

    fun ghostDrop() {
        while (softDrop(ghostPiece)) {
            // works as intended without anything here
        }
    }

    fun moveLeft(): Boolean {
        if (isFreeOfCollisions(-1, 0)) {
            activePiece.moveLeft()
            ghostPiece = activePiece.clone(activePiece.direction)
            return true
        }
        return false
    }

    fun moveRight(): Boolean {
        if (isFreeOfCollisions(1, 0)) {
            activePiece.moveRight()
            ghostPiece = activePiece.clone(activePiece.direction)
            return true
        }
        return false
    }

    fun rotateClockwise(): Boolean {
        if (isFreeOfCollisions(activePiece.direction - 1)) {
            activePiece.rotateClockwise()
            ghostPiece = activePiece.clone(activePiece.direction)
            return true
        }
        return false
    }

    fun rotateCounterClockwise(): Boolean {
        if (isFreeOfCollisions(activePiece.direction + 1)) {
            activePiece.rotateCounterClockwise()
            ghostPiece = activePiece.clone(activePiece.direction)
            return true
        }
        return false
    }

    fun placePiece() {
        updatePiece(staticGrid)
        clearCheck()
        getNextPiece()
        holdUsed = false
    }

    fun isFreeOfCollisions(dx: Int, dy: Int): Boolean {
        val pieceCoordArray = activePiece.getCoordinates()
        for (coord in pieceCoordArray) {
            val x = coord % 4 + activePiece.coords.x + dx
            val y = coord / 4 + activePiece.coords.y + dy
            if (y < 0 || x < 0 || x > 9 || y > 19 || staticGrid[y][x].isFilled) {
                return false
            }
        }
        return true
    }

    fun isFreeOfCollisions(direction: Int): Boolean {
        val dir = activePiece.direction
        val pieceCoordArray = activePiece.getCoordinates(direction)
        for (coord in pieceCoordArray) {
            val x = coord % 4 + activePiece.coords.x
            val y = coord / 4 + activePiece.coords.y
            if (x < 0 || x > 9 || y < 0 || y > 19 || staticGrid[y][x].isFilled) {
                activePiece.direction = dir
                return false
            }
        }
        activePiece.direction = dir
        return true
    }

    private fun updatePiece(grid: ArrayList<Array<Cell>>) {
        val pieceCoordArray = activePiece.getCoordinates()
        for (coord in pieceCoordArray) {
            val x = coord % 4 + activePiece.coords.x
            val y = coord / 4 + activePiece.coords.y
            grid[y][x] = Cell(true, activePiece.color)
        }
    }

    private fun updateGhostPiece() {
        ghostPiece = activePiece.clone(activePiece.direction)
        ghostDrop()
        val pieceCoordArray = ghostPiece.getCoordinates()
        for (coord in pieceCoordArray) {
            val x = coord % 4 + ghostPiece.coords.x
            val y = coord / 4 + ghostPiece.coords.y
            val c = Color(ghostPiece.color.red, ghostPiece.color.green, ghostPiece.color.blue, 35)
            ghostGrid[y][x] = Cell(true, c)
        }
    }

    private fun updatePiece() {
        updatePiece(activeGrid)
        updateGhostPiece()
    }

    private fun clearCheck() {
        val pog = ArrayList<Array<Cell>>()
        for (row in staticGrid) {
            val isFullRow = row.all { it.isFilled }
            if (isFullRow) {
                pog.add(row)
            }
        }
        for (row in pog) {
            linesCleared++
            staticGrid.remove(row)
            staticGrid.add(0, Array(numCols) { Cell(false, emptyColor) })
        }
    }

    fun update() {
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                activeGrid[row][col].isFilled = false
                ghostGrid[row][col].isFilled = false
            }
        }
        updatePiece()
    }

    fun holdPiece() {
        if (!holdUsed) {
            val temp = holdPiece
            holdPiece = activePiece
            holdPiece!!.coords.setLocation(4, 0)
            holdPiece!!.direction = 0
            if (temp != null) {
                activePiece = temp
            } else {
                getNextPiece()
            }
            holdUsed = true
        }
    }

    fun getDropSpeed(): Int {
        val linesPerLevel = 20 // TODO: adjust this (and learn what tetris actually uses for levels)
        val level = linesCleared / linesPerLevel + 1
        return level
        // TODO: make the speed scale properly
        return when (level) {
            1 -> 1

            else -> 50
        }

    }
}

data class Cell(var isFilled: Boolean, var color: Color)