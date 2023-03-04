import java.util.stream.IntStream.range
import kotlin.streams.toList

class Board {
    val staticGrid: ArrayList<Array<Cell>> = ArrayList()
    val activeGrid: ArrayList<Array<Cell>> = ArrayList()
    val ghostGrid: ArrayList<Array<Cell>> = ArrayList()
    val spawnRows = 0 // for piece spawning
    val numCols = 10
    val numRows = 20 + spawnRows
    val numDisplayedRows = numRows - spawnRows
    val nextPieces: ArrayList<Piece> = ArrayList() // 1 more than shows on screen
    var pieceBag: ArrayList<Piece> = ArrayList()

    var hasGhostPiece = true
    var holdUsed = false
    var holdPiece: Piece? = null
    var activePiece: Piece
    var ghostPiece: Piece

    var linesCleared = 0

    init {
        for (i in 0 until numRows) {
            staticGrid.add(Array(numCols) { Cell(PieceType.EMPTY) })
            activeGrid.add(Array(numCols) { Cell(PieceType.EMPTY) })
            ghostGrid.add(Array(numCols) { Cell(PieceType.EMPTY) })
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
        val randNums = range(0, pieceList.size).toList().shuffled()
        val pieces = ArrayList<Piece>()
        for (rand in randNums) {
            pieces.add(pieceList[rand])
        }
        return pieces
    }

    fun generatePiece(): Piece {
        if (pieceBag.size == 0) {
            pieceBag.addAll(generateShuffledBag())
        }
        val newPiece = pieceBag[0]
        pieceBag.removeAt(0)
        return newPiece
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
            if (y >= numRows || staticGrid[y][x].isFilled) {
                if (piece === activePiece) {
                    placePiece()
                }
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
            if (y < 0 || x < 0 || x >= numCols || y >= numRows || staticGrid[y][x].isFilled) {
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
            if (x < 0 || x >= numCols || y < 0 || y >= numRows || staticGrid[y][x].isFilled) {
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
            grid[y][x] = Cell(activePiece.type)
        }
        updateGhostPiece()
    }

    private fun updateGhostPiece() {
        if (!hasGhostPiece) {
            return
        }
        ghostPiece = activePiece.clone(activePiece.direction)
        ghostDrop() // TODO: figure out what the bug with this is (it keeps crashing)
        val pieceCoordArray = ghostPiece.getCoordinates()
        for (coord in pieceCoordArray) {
            val x = coord % 4 + ghostPiece.coords.x
            val y = coord / 4 + ghostPiece.coords.y
            ghostGrid[y][x] = Cell(ghostPiece.type)
        }
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
            //println("lines cleared: $linesCleared")
            staticGrid.remove(row)
            staticGrid.add(0, Array(numCols) { Cell(PieceType.EMPTY) })
        }
    }

    fun update() {
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                activeGrid[row][col].pieceType = PieceType.EMPTY
                ghostGrid[row][col].pieceType = PieceType.EMPTY
            }
        }
        updatePiece(activeGrid)
    }

    fun holdPiece() {
        if (!holdUsed) {
            val temp = holdPiece
            holdPiece = when (activePiece) {
                is JPiece -> JPiece()
                is LPiece -> LPiece()
                is OPiece -> OPiece()
                is SPiece -> SPiece()
                is TPiece -> TPiece()
                is ZPiece -> ZPiece()
                else -> IPiece()
            }
            if (temp != null) {
                activePiece = temp
            } else {
                getNextPiece()
            }
            holdUsed = true
        }
    }

    fun getDropSpeed(): Int {
        val linesPerLevel = 10 // TODO: adjust this (and learn what tetris actually uses for levels)
        val level = linesCleared / linesPerLevel + 1
        return level
        // TODO: make the speed scale properly
//        return when (level) {
//            1 -> 1
//
//            else -> 50
//        }

    }
}

data class Cell(var pieceType: PieceType) {
    val isFilled: Boolean
        get() = pieceType != PieceType.EMPTY
}