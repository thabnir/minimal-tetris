import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import com.formdev.flatlaf.util.SystemInfo
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities


/*

// TODO: figure out how it crashed those two times (index out of bounds for a ghost piece dropping)

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
    // TODO: correctly order all of this shit (system properties and flatlafs and the like). it is a mess rn
    // it's probably not a big deal because the flaws are behind the scenes, but it could become a problem later on
    val isDark = true
    // macOS  (see https://www.formdev.com/flatlaf/macos/)
    if (SystemInfo.isMacOS) {
        // enable screen menu bar [i think this is an instruction for where to enable it]
        // (moves menu bar from JFrame window to top of screen)
        System.setProperty("apple.laf.useScreenMenuBar", "true")

        // application name used in screen menu bar
        // (in first menu after the "apple" menu)
        System.setProperty("apple.awt.application.name", "Tetris")

        // appearance of window title bars
        // possible values:
        //   - "system": use current macOS appearance (light or dark)
        //   - "NSAppearanceNameAqua": use light appearance
        //   - "NSAppearanceNameDarkAqua": use dark appearance
        // (needs to be set on main thread; setting it on AWT thread does not work)
        if (isDark) System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua")
        else System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua")
    }
    if (isDark) FlatDarkLaf.setup() else FlatIntelliJLaf.setup()
    val g = GraphicsPanel()
    g.run() // NOTE: DO NOT CALL RUN IN INITIALIZER (something to do with threads maybe? it won't work if you do)
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
        screenSize.width / 10 - screenSize.height / 10 % 20, screenSize.height / 5 - screenSize.height / 5 % 20
    )
    val board = Board()
    val gameBoardPanel = GameBoardPanel(this)
    val uiBoardPanel = UIBoardPanel(this)

    init {
        mainFrameMac()
        // allWindowsMac()
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
        frame.preferredSize = gameBoardPanel.preferredSize
        frame.minimumSize = minSize
        frame.maximumSize = screenSize
        frame.background = board.emptyColor

        //frame.add(uiBoardPanel) // TODO: this (swap the add order with gbp to see the problem)

        frame.add(gameBoardPanel)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        frame.pack()
        frame.layout = BorderLayout() // prevents the frame from resizing itself
        frame.isResizable = false
        frame.setLocationRelativeTo(null)
        frame.validate()
        frame.isVisible = true
    }

    fun mainFrameMac() {
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            if (SystemInfo.isMacFullWindowContentSupported) {
                // expand window content into window title bar and make title bar transparent
                frame.rootPane.putClientProperty("apple.awt.fullWindowContent", true)

                // hide window title
                if (SystemInfo.isJava_17_orLater) {
                    frame.rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
                } else {
                    frame.title = null
                }
            }

            // enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
            if (!SystemInfo.isJava_11_orLater) {
                frame.rootPane.putClientProperty("apple.awt.fullscreenable", true)
            }

            if (SystemInfo.isMacFullWindowContentSupported) {
                frame.rootPane.putClientProperty("apple.awt.transparentTitleBar", true) // transparent title bar
            }
        }
    }

    fun allWindowsMac() {
        val isDark = board.emptyColor == Color.BLACK
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            // enable screen menu bar
            // (moves menu bar from JFrame window to top of screen)
            System.setProperty("apple.laf.useScreenMenuBar", "true")

            // application name used in screen menu bar
            // (in first menu after the "apple" menu)
            System.setProperty("apple.awt.application.name", "Tetris")

            // appearance of window title bars
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (needs to be set on main thread; setting it on AWT thread does not work)
            if (isDark) System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua")
            else System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua")
        }
        SwingUtilities.invokeLater {
            if (isDark) FlatDarkLaf.setup() else FlatIntelliJLaf.setup()
        }
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
        //allWindowsMac()
        w = panel.gameBoardPanel.width / panel.board.numCols
        h = panel.gameBoardPanel.height / panel.board.numRows
        this.preferredSize = Dimension(w * 4, h * 4)
        this.isVisible = true
        validate()
    }

    fun allWindowsMac() {
        val isDark = panel.board.emptyColor == Color.BLACK
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            // enable screen menu bar
            // (moves menu bar from JFrame window to top of screen)
            System.setProperty("apple.laf.useScreenMenuBar", "true")

            // application name used in screen menu bar
            // (in first menu after the "apple" menu)
            System.setProperty("apple.awt.application.name", "Tetris")

            // appearance of window title bars
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (needs to be set on main thread; setting it on AWT thread does not work)
            if (isDark) System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua")
            else System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua")
        }
        SwingUtilities.invokeLater {
            if (isDark) FlatDarkLaf.setup() else FlatIntelliJLaf.setup()
        }
    }
}

class GameBoardPanel(val panel: GraphicsPanel) : JPanel() {
    val outlineColor: Color = panel.board.emptyColor

    // TODO: make these values configurable by menu sliders [it would be fun, not super necessary though]
    val bevelAmount = 2 // lower is rounder
    val gap = 1 // gap between cells
    val t = 1 // ghost piece line thickness (if it's an outline)
    val isRound = false
    val ghostPieceIsOutline = true

    init {
        // allWindowsMac()
        preferredSize = panel.prefSize
        isVisible = true
        validate()
        //this.requestFocusInWindow()
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val w = width / panel.board.numCols
        val h = height / panel.board.numRows
        val bevelSize = w / bevelAmount

        g.color = panel.board.emptyColor
        g.fillRect(0, 0, width, height) // background

        if (ghostPieceIsOutline) {
            for (row in 0 until panel.board.numRows) {
                for (col in 0 until panel.board.numCols) {
                    var isEmpty = false
                    var isGhost = false
                    if (panel.board.activeGrid[row][col].isFilled) {
                        g.color = panel.board.activeGrid[row][col].color
                    } else if (panel.board.staticGrid[row][col].isFilled) {
                        g.color = panel.board.staticGrid[row][col].color
                    } else if (panel.board.ghostGrid[row][col].isFilled) {
                        var c = panel.board.ghostGrid[row][col].color
                        c = Color(c.red, c.green, c.blue)
                        g.color = c
                        isGhost = true
                    } else {
                        isEmpty = true
                    }
                    val leftEdge = col * w + gap
                    val topEdge = row * h + gap
                    val wi = w - gap * 2 + gap
                    val he = h - gap * 2 + gap
                    if (isRound) {
                        if (!isEmpty && !isGhost) {
                            g.fillRoundRect(leftEdge, topEdge, wi, he, bevelSize, bevelSize)
                        } else if (isGhost) {
                            g.fillRoundRect(leftEdge, topEdge, wi, he, bevelSize, bevelSize)
                            g.color = panel.board.emptyColor
                            g.fillRoundRect(leftEdge + t,topEdge + t, wi - 2 * t, he - 2 * t, bevelSize, bevelSize)
                        }
                    } else if (!isEmpty && !isGhost) {
                        g.fillRect(leftEdge, topEdge, wi, he)
                    } else if (isGhost) {
                        g.fillRect(leftEdge, topEdge, wi, he) // (for some reason using drawRect sucks ass and is ugly)
                        g.color = panel.board.emptyColor
                        g.fillRect(leftEdge + t, topEdge + t, wi - 2 * t, he - 2 * t)
                    }
                }
            }
        } else {
            for (row in 0 until panel.board.numRows) {
                for (col in 0 until panel.board.numCols) {
                    var isFilled = true
                    if (panel.board.activeGrid[row][col].isFilled) {
                        g.color = panel.board.activeGrid[row][col].color
                    } else if (panel.board.staticGrid[row][col].isFilled) {
                        g.color = panel.board.staticGrid[row][col].color
                    } else if (panel.board.ghostGrid[row][col].isFilled) {
                        g.color = panel.board.ghostGrid[row][col].color
                    } else {
                        isFilled = false
                    }
                    if (isRound) {
                        val r = w / 7
                        g.fillRoundRect(col * w, row * h, w, h, r, r)
                        g.color = outlineColor
                        g.drawRoundRect(col * w, row * h, w, h, r, r)
                    } else if (isFilled) {
                        g.fillRect(col * w, row * h, w, h)
                        g.color = outlineColor
                        g.drawRect(col * w, row * h, w, h)
                    }
                }
            }
        }
    }

    fun allWindowsMac() {
        val isDark = panel.board.emptyColor == Color.BLACK
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            // enable screen menu bar
            // (moves menu bar from JFrame window to top of screen)
            System.setProperty("apple.laf.useScreenMenuBar", "true")

            // application name used in screen menu bar
            // (in first menu after the "apple" menu)
            System.setProperty("apple.awt.application.name", "Tetris")

            // appearance of window title bars
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (needs to be set on main thread; setting it on AWT thread does not work)
            if (isDark) System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua")
            else System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua")
        }
        SwingUtilities.invokeLater {
            if (isDark) FlatDarkLaf.setup() else FlatIntelliJLaf.setup()
        }
    }
}