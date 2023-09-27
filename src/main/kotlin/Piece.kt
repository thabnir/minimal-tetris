import java.awt.Point

enum class PieceType {
    L,
    J,
    I,
    T,
    O,
    S,
    Z,
    EMPTY
}

abstract class Piece(val type: PieceType, var direction: Int, var coords: Point) {
    abstract fun rotateCounterClockwise()
    abstract fun rotateClockwise()
    abstract fun getCoordinates(dir: Int): Array<Int>
    abstract fun getCoordinates(): Array<Int>
    fun moveDown() {
        coords.translate(0, 1)
    }

    fun moveLeft() {
        coords.translate(-1, 0)
    }

    fun moveRight() {
        coords.translate(1, 0)
    }

    fun clone(direction: Int): Piece {
        // TODO: make this less shitty perhaps? fixing it might be more work than it's worth
        val piece = when (this) {
            is LPiece -> LPiece()
            is JPiece -> JPiece()
            is IPiece -> IPiece()
            is TPiece -> TPiece()
            is OPiece -> OPiece()
            is SPiece -> SPiece()
            is ZPiece -> ZPiece()
            else -> error("Unknown piece type")
        }
        piece.direction = direction
        piece.coords = Point(this.coords.x, this.coords.y)
        return piece
    }
}

class LPiece : Piece(PieceType.L, 0, Point(3, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 4
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 4)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return when (Math.floorMod(dir, 4)) {
            0 -> arrayOf(3, 5, 6, 7)
            1 -> arrayOf(1, 2, 6, 10)
            2 -> arrayOf(5, 6, 7, 9)
            3 -> arrayOf(2, 6, 10, 11)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }
}

class JPiece : Piece(PieceType.J, 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 4
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 4)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return when (Math.floorMod(dir, 4)) {
            0 -> arrayOf(0, 4, 5, 6)
            1 -> arrayOf(1, 5, 9, 8)
            2 -> arrayOf(4, 5, 6, 10)
            3 -> arrayOf(1, 2, 5, 9)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }

}

class IPiece : Piece(PieceType.I, 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 4
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 4)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return when (Math.floorMod(dir, 4)) {
            0 -> arrayOf(4, 5, 6, 7)
            1 -> arrayOf(2, 6, 10, 14)
            2 -> arrayOf(8, 9, 10, 11)
            3 -> arrayOf(1, 5, 9, 13)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }
}

class TPiece : Piece(PieceType.T, 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 4
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 4)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return when (Math.floorMod(dir, 4)) {
            0 -> arrayOf(1, 4, 5, 6)
            1 -> arrayOf(1, 4, 5, 9)
            2 -> arrayOf(4, 5, 6, 9)
            3 -> arrayOf(1, 5, 6, 9)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }
}

class OPiece : Piece(PieceType.O, 0, Point(3, 0)) {
    override fun rotateCounterClockwise() {
        // squares can't rotate
    }

    override fun rotateClockwise() {
        // squares can't rotate
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return arrayOf(1, 2, 5, 6)
    }

    override fun getCoordinates(): Array<Int> {
        return arrayOf(1, 2, 5, 6)
    }
}

class SPiece : Piece(PieceType.S, 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 2
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 2)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        // UNTESTED COORDS
        return when (Math.floorMod(dir, 2)) {
            0 -> arrayOf(1, 2, 4, 5)
            1 -> arrayOf(0, 4, 5, 9)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }
}

class ZPiece : Piece(PieceType.Z, 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 2
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 2)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        // UNTESTED COORDS
        return when (Math.floorMod(dir, 2)) {
            0 -> arrayOf(0, 1, 5, 6)
            1 -> arrayOf(1, 4, 5, 8)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }
}