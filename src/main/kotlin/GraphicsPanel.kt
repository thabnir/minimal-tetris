import com.formdev.flatlaf.util.SystemInfo
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep
import javax.swing.*


/*
TODO: figure out how it crashed those two times (index out of bounds for a ghost piece dropping)

TODO: fix collisions (bounce off edges when spinning, verify which spins you can do)
TODO: make the pieces spawn in the correct place (above the screen I believe)
TODO: make auto-repeat delay and rate customizable and unlinked from key repeat speed
TODO: add a lose state (when the next piece is spawned in the same place as an existing piece)
TODO: add delay between when the active piece collides downward and when it actually places
    could try using a timer to delay the placement of the piece
    could try using a counter that downticks every time it tries to soft-drop and partially refreshes upon moving

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

class GraphicsPanel : JPanel() {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    var theme = Theme.BABY
    var paused = false
    val frame = JFrame("Tetris")
    val board = Board()
    val menuBar: JMenuBar
    val gameBoardPanel = GameBoardPanel(this)

    init {
        mainFrameMac()
        menuBar = createTableMenuBar()
        frame.jMenuBar = menuBar

        frame.iconImage = ImageIcon(javaClass.getResource("tetris_logo.png")).image // doesn't do anything?

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
                }
                if (e.keyCode == pauseKey) {
                    paused = !paused
                }
            }

            override fun keyTyped(e: KeyEvent) {
                // unused
            }

            override fun keyReleased(e: KeyEvent) {
                // unused (maybe use for holding a movement button down to slide a piece?)
            }
        })
        //frame.preferredSize = gameBoardPanel.preferredSize
        //frame.maximumSize = screenSize
        frame.background = theme.backgroundColor
        frame.add(gameBoardPanel)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.layout = BorderLayout() // prevents the frame from resizing itself
        frame.isResizable = true
        frame.setLocationRelativeTo(null)
        frame.validate()
        frame.isVisible = true
        frame.minimumSize = Dimension(gameBoardPanel.prefSize.width, gameBoardPanel.prefSize.height + menuBar.height)
    }

    fun mainFrameMac() {
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            if (SystemInfo.isMacFullWindowContentSupported) {
                // expand window content into window title bar and make title bar transparent
                frame.rootPane.putClientProperty("apple.awt.fullWindowContent", Theme.yee)
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

    val holdKey = KeyEvent.VK_C
    val pauseKey = KeyEvent.VK_ESCAPE
    val rotateCounterClockwiseKey = KeyEvent.VK_Z
    val rotateClockwiseKey = KeyEvent.VK_UP
    val moveRightKey = KeyEvent.VK_RIGHT
    val moveLeftKey = KeyEvent.VK_LEFT
    val softDropKey = KeyEvent.VK_DOWN
    val hardDropKey = KeyEvent.VK_SPACE

    val startTime = currentTimeMillis()

    val tickCount: Long
        get() = currentTimeMillis() - startTime

    fun run() {
        // NOTE: DO NOT CALL THIS METHOD IN GRAPHICS PANEL INITIALIZER
        gameBoardPanel.isDoubleBuffered = true // idk if this does anything tbh
        var skipTicks = (1000 / board.getDropSpeed() / .6).toInt()
        val maxFrameSkip = 10
        var nextGameTick = tickCount
        var loops: Int

        while (true) {

            while (!paused) {
                loops = 0
                while (tickCount > nextGameTick && loops < maxFrameSkip) {
                    skipTicks = (1000 / board.getDropSpeed() / .6).toInt()
                    board.softDrop()
                    gameBoardPanel.repaint()
                    nextGameTick += skipTicks
                    loops++
                }
                board.update()
                gameBoardPanel.repaint()
                sleep(5)
            }

            loops = 0
            while (tickCount > nextGameTick && loops < maxFrameSkip) {
                nextGameTick += skipTicks
                loops++
            }
            sleep(50)
        }
    }


    private fun createTableMenuBar(): JMenuBar {
        val menuBar = JMenuBar()
        // menuBar.add(createGameplayPrefsMenu())
        menuBar.add(createThemeMenu())
        menuBar.add(createVisualPrefsMenu())
        menuBar.validate()
        return menuBar
    }

    private fun createGameplayPrefsMenu(): JMenu {
        val preferencesMenu = JMenu("Gameplay")
        // do stuff about level select, key repeat time, etc.
        return preferencesMenu
    }

    fun createVisualPrefsMenu(): JMenu {
        val menu = JMenu("Visual Preferences")

        val gapLabel = JLabel("Gap: " + Theme.gap)
        val gapSlider = JSlider(JSlider.HORIZONTAL, 0, 50, Theme.gap)
        gapSlider.addChangeListener {
            Theme.gap = gapSlider.value // doesn't show up on the menu for some reason
            gapLabel.text = "Gap: " + Theme.gap
        }

        val bevelLabel = JLabel("Roundness: " + Theme.bevelPercent)
        val bevelSlider = JSlider(JSlider.HORIZONTAL, 0, 100, (Theme.bevelPercent * 100).toInt())
        bevelSlider.addChangeListener {
            Theme.bevelPercent = bevelSlider.value.toFloat() / 100 // doesn't show up on the menu for some reason
            bevelLabel.text = "Roundness: " + Theme.bevelPercent
        }

        val hasGhostPieceItem = JCheckBoxMenuItem("Ghost Piece", board.hasGhostPiece)
        hasGhostPieceItem.addActionListener {
            board.hasGhostPiece = hasGhostPieceItem.isSelected
        }

        val darkModeMenuItem = JCheckBoxMenuItem("Dark", Theme.isDarkMode)
        darkModeMenuItem.addActionListener {
            Theme.isDarkMode = darkModeMenuItem.isSelected
            // if (Theme.isDarkMode) FlatLaf.setup(FlatDarkLaf()) else FlatIntelliJLaf.setup(FlatLightLaf())
        }

        val isGhostOutlinedItem = JCheckBoxMenuItem("Ghost Outlined", Theme.isWireframeGhostPiece)
        isGhostOutlinedItem.addActionListener {
            Theme.isWireframeGhostPiece = isGhostOutlinedItem.isSelected
        }

        val isRoundedItem = JCheckBoxMenuItem("Rounded", Theme.isRound)
        isRoundedItem.addActionListener {
            Theme.isRound = isRoundedItem.isSelected
        }
        menu.add(darkModeMenuItem)
        menu.add(hasGhostPieceItem)
        menu.add(isGhostOutlinedItem)
        menu.add(bevelLabel)
        menu.add(bevelSlider)
        menu.add(gapLabel)
        menu.add(gapSlider) // doesn't work when bar is at top of screen
        return menu
    }

    private fun createThemeMenu(): JMenu {
        val themeMenu = JMenu("Theme")
        val radioGroup = ButtonGroup()
        enumValues<Theme>().forEach { t ->
            val item = JRadioButtonMenuItem(t.printableName)
            item.isSelected = t == theme
            item.addActionListener {
                theme = t
            }
            radioGroup.add(item)
            themeMenu.add(item)
        }
        return themeMenu
    }
}