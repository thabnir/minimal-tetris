import java.awt.Color
import java.awt.Point

// 0 is default
// 1 is 3 o clock
// 2 is 6 o clock
// 3 is 9 o clock

/*
      COORDS:
    0  1  2  3
    4  5  6  7
    8  9  10 11
    12 13 14 15
 */
abstract class Piece(val color: Color, var direction: Int, var coords: Point) {
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
        val p: Piece
        when (this) {
            is LPiece -> {
                p = LPiece()
                p.direction = direction
                p.coords = Point(this.coords.x, this.coords.y)
            }
            is JPiece -> {
                p = JPiece()
                p.direction = direction
                p.coords = Point(this.coords.x, this.coords.y)
            }
            is IPiece -> {
                p = IPiece()
                p.direction = direction
                p.coords = Point(this.coords.x, this.coords.y)
            }
            is TPiece -> {
                p = TPiece()
                p.direction = direction
                p.coords = Point(this.coords.x, this.coords.y)
            }
            is OPiece -> {
                p = OPiece()
                p.direction = direction
                p.coords = Point(this.coords.x, this.coords.y)
            }
            is SPiece -> {
                p = SPiece()
                p.direction = direction
                p.coords = Point(this.coords.x, this.coords.y)
            }
            is ZPiece -> {
                p = ZPiece()
                p.direction = direction
                p.coords = Point(this.coords.x, this.coords.y)
            }
            else -> {
                error("Unknown piece type")
            }
        }
        return p
    }
}

class LPiece : Piece(Color(226, 156, 1), 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 4
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 4)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return when (Math.floorMod(dir, 4)) {
            0 -> arrayOf(1, 2, 6, 10)
            1 -> arrayOf(5, 6, 7, 9)
            2 -> arrayOf(2, 6, 10, 11)
            3 -> arrayOf(3, 5, 6, 7)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }
}

class JPiece : Piece(Color(1, 130, 233), 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 4
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 4)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return when (Math.floorMod(dir, 4)) {
            0 -> arrayOf(1, 2, 5, 9)
            1 -> arrayOf(0, 4, 5, 6)
            2 -> arrayOf(1, 5, 9, 8)
            3 -> arrayOf(4, 5, 6, 10)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }

}

class IPiece : Piece(Color(3, 201, 223), 0, Point(4, 0)) {
    override fun rotateCounterClockwise() {
        direction = (direction + 1) % 2
    }

    override fun rotateClockwise() {
        direction = Math.floorMod((direction - 1), 2)
    }

    override fun getCoordinates(dir: Int): Array<Int> {
        return when (Math.floorMod(dir, 2)) {
            0 -> arrayOf(1, 5, 9, 13)
            1 -> arrayOf(4, 5, 6, 7)
            else -> arrayOf(-1, -1, -1, -1)
        }
    }

    override fun getCoordinates(): Array<Int> {
        return getCoordinates(direction)
    }
}

class TPiece : Piece(Color(178, 0, 215), 0, Point(4, 0)) {
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

class OPiece : Piece(Color(240, 230, 0), 0, Point(4, 0)) {
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

class SPiece : Piece(Color(6, 231, 67), 0, Point(4, 0)) {
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

class ZPiece : Piece(Color(237, 1, 0), 0, Point(4, 0)) {
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