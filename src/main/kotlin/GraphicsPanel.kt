import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep
import javax.swing.JFrame
import javax.swing.JPanel

/*
TODO: fix collisions (bounce off edges when spinning, verify which spins you can do)
TODO: make the pieces spawn in the correct place (above the screen I believe)
TODO: make auto-repeat delay and rate customizable and unlinked from key repeat speed
TODO: add a lose state (when the next piece is spawned in the same place as an existing piece)
TODO: add delay between when the active piece collides downward and when it actually places
    // could try using a timer to delay the placement of the piece
    // could try using a counter that downticks every time it tries to soft-drop and partially refreshes upon moving

TODO: add hold piece visual
TODO: add next pieces up visual
TODO: add score system + display it (with high scores)
TODO: add background + ui elements that work with the rounded corners of mac windows
TODO: add sound + visual effects (very optional)

TODO: add a new game / restart button (with level select)
TODO: add a visual pause button
TODO: add a preferences menu (for controls, ghost pieces, auto-repeat settings, sfx, vfx, music, etc.)
TODO: make selectable background color (that properly works with the background color of the frame)
 */


fun main() {
    val g = GraphicsPanel()
    g.run() // NOTE: DO NOT CALL RUN IN INITIALIZER
}

class GraphicsPanel : JPanel() {
    var paused = false
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val frame = JFrame("Tetris")
    val prefSize = Dimension(
        screenSize.height / 3 - screenSize.height / 3 % 20,
        (screenSize.height / 1.5).toInt() - (screenSize.height / 1.5).toInt() % 20
    )
    val minSize = Dimension(
        screenSize.width / 10 - screenSize.height / 10 % 20,
        screenSize.height / 5 - screenSize.height / 5 % 20
    )
    val board = Board()
    val gameBoardPanel = GameBoardPanel(this)

    val uiBoardPanel = UIBoardPanel(this)

    init {
        frame.addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                if (!paused) {
                    when (e.keyCode) {
                        softDropKey -> board.softDrop()
                        moveRightKey -> board.moveRight()
                        moveLeftKey -> board.moveLeft()
                        hardDropKey -> board.hardDrop()
                        rotateCounterClockwiseKey -> board.rotateCounterClockwise()
                        rotateClockwiseKey -> board.rotateClockwise()
                        holdKey -> board.holdPiece()
                    }
                    gameBoardPanel.repaint()
                }
                if (e.keyCode == pauseKey) {
                    paused = !paused
                }
            }

            override fun keyTyped(e: KeyEvent) {
                // unused
            }
            override fun keyReleased(e: KeyEvent) {
                // unused (maybe use for holding button down and sliding piece?)
            }
        })
        //System.setProperty("sun.java2d.uiScale", "1.0")

        frame.preferredSize = gameBoardPanel.preferredSize
        frame.minimumSize = minSize
        frame.maximumSize = screenSize
        frame.background = board.emptyColor

        frame.add(uiBoardPanel) // TODO: this (swap the add order with gbp to see the problem)

        frame.add(gameBoardPanel)
        frame.pack()
        frame.layout = BorderLayout()
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isResizable = true
        frame.setLocationRelativeTo(null)
        frame.validate()
        frame.isVisible = true
    }

    val holdKey = KeyEvent.VK_C
    val pauseKey = KeyEvent.VK_ESCAPE
    val rotateCounterClockwiseKey = KeyEvent.VK_Z
    val rotateClockwiseKey = KeyEvent.VK_UP
    val moveRightKey = KeyEvent.VK_RIGHT
    val moveLeftKey = KeyEvent.VK_LEFT
    val softDropKey = KeyEvent.VK_DOWN
    val hardDropKey = KeyEvent.VK_SPACE

    val startTime = currentTimeMillis()
    fun getTickCount(): Long {
        return currentTimeMillis() - startTime
    }

    fun run() {
        // NOTE: DO NOT CALL THIS METHOD IN GRAPHICS PANEL INITIALIZER
        gameBoardPanel.isDoubleBuffered = true
        var skipTicks = 1000 / board.getDropSpeed()
        val maxFrameSkip = 10
        var nextGameTick = getTickCount()
        var loops: Int

        while (true) {

            while (!paused) {
                loops = 0
                while (getTickCount() > nextGameTick && loops < maxFrameSkip) {
                    skipTicks = 1000 / board.getDropSpeed() // find some way to speed it up
                    board.softDrop()
                    gameBoardPanel.repaint()
                    nextGameTick += skipTicks
                    loops++
                }
                board.update()
                gameBoardPanel.repaint()
                sleep(1)
            }

            loops = 0
            while (getTickCount() > nextGameTick && loops < maxFrameSkip) {
                nextGameTick += skipTicks
                loops++
            }
            sleep(50)
        }
    }
}

class UIBoardPanel(val panel: GraphicsPanel) : JPanel() {
    val w: Int
    val h: Int

    init {
        System.setProperty("sun.java2d.uiScale", "1.0")
        w = panel.gameBoardPanel.width / panel.board.numCols
        h = panel.gameBoardPanel.height / panel.board.numRows
        this.preferredSize = Dimension(w * 4, h * 4)
        this.isVisible = true
        validate()
    }
}

class GameBoardPanel(val panel: GraphicsPanel) : JPanel() {
    val isRound = false
    val outlineColor = panel.board.emptyColor

    init {
        System.setProperty("sun.java2d.uiScale", "1.0")
        preferredSize = panel.prefSize
        isVisible = true
        validate()
        //this.requestFocusInWindow()
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val w = width / panel.board.numCols
        val h = height / panel.board.numRows
        for (row in 0 until panel.board.numRows) {
            for (col in 0 until panel.board.numCols) {
                if (panel.board.activeGrid[row][col].isFilled) {
                    g.color = panel.board.activeGrid[row][col].color
                } else if (panel.board.staticGrid[row][col].isFilled) {
                    g.color = panel.board.staticGrid[row][col].color
                } else if (panel.board.ghostGrid[row][col].isFilled) {
                    g.color = panel.board.ghostGrid[row][col].color
                } else {
                    g.color = panel.board.emptyColor
                }
                if (isRound) {
                    val r = w / 7
                    g.fillRoundRect(col * w, row * h, w, h, r, r)
                    g.color = outlineColor
                    g.drawRoundRect(col * w, row * h, w, h, r, r)
                } else {
                    g.fillRect(col * w, row * h, w, h)
                    g.color = outlineColor
                    g.drawRect(col * w, row * h, w, h)
                }
            }
        }
    }
}