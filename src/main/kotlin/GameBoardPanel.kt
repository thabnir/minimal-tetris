import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import com.formdev.flatlaf.util.SystemInfo
import java.awt.*
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.roundToInt

class GameBoardPanel(val panel: GraphicsPanel) : JPanel() {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val screenRatio = .24
    val squareWidth = ((screenSize.width * screenRatio) / panel.board.numCols).toInt()
    val prefSize = Dimension(squareWidth * panel.board.numCols, squareWidth * panel.board.numDisplayedRows)

    init {
        // allWindowsMac()
        preferredSize = prefSize
        isVisible = true
        validate()
        //this.requestFocusInWindow()
    }

    public override fun paintComponent(g: Graphics) {
        if (Theme.isRound) {
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        }
        super.paintComponent(g)
        background = panel.theme.backgroundColor
        val w = width / panel.board.numCols
        val h = height / panel.board.numDisplayedRows
        val bevelSize = (w * Theme.bevelPercent).roundToInt()
        for (row in panel.board.spawnRows until panel.board.numRows) {
            for (col in 0 until panel.board.numCols) {
                var isGhost = false
                if (panel.board.activeGrid[row][col].isFilled) {
                    g.color = getPieceColor(panel.board.activeGrid[row][col].pieceType)
                } else if (panel.board.staticGrid[row][col].isFilled) {
                    g.color = getPieceColor(panel.board.staticGrid[row][col].pieceType)
                } else if (panel.board.ghostGrid[row][col].isFilled) {
                    val c = getPieceColor(panel.board.ghostGrid[row][col].pieceType)
                    g.color = Color(c.red, c.green, c.blue, panel.theme.ghostPieceAlpha)
                    isGhost = true
                } else {
                    // g.color = panel.theme.backgroundColor
                    continue
                }
                val leftEdge = col * w + Theme.gap / 2
                val topEdge = (row - panel.board.spawnRows) * h + Theme.gap / 2
                val wi = w - Theme.gap
                val he = h - Theme.gap
                if (Theme.isRound) {
                    g.fillRoundRect(leftEdge, topEdge, wi, he, bevelSize, bevelSize)
                    if (isGhost && Theme.isWireframeGhostPiece) {
                        g.color = panel.theme.backgroundColor
                        g.fillRoundRect(
                            leftEdge + Theme.borderThickness,
                            topEdge + Theme.borderThickness,
                            wi - 2 * Theme.borderThickness,
                            he - 2 * Theme.borderThickness,
                            bevelSize, bevelSize
                        )
                    }
                } else {
                    g.fillRect(leftEdge, topEdge, wi, he) // drawRect sucks, IDK why
                    if (isGhost && Theme.isWireframeGhostPiece) {
                        g.color = panel.theme.backgroundColor
                        g.fillRect(
                            leftEdge + Theme.borderThickness,
                            topEdge + Theme.borderThickness,
                            wi - 2 * Theme.borderThickness,
                            he - 2 * Theme.borderThickness
                        )
                    }
                }
            }
        }
    }

    fun getPieceColor(pieceType: PieceType): Color {
        return when (pieceType) {
            PieceType.L -> panel.theme.lColor
            PieceType.J -> panel.theme.jColor
            PieceType.I -> panel.theme.iColor
            PieceType.T -> panel.theme.tColor
            PieceType.O -> panel.theme.oColor
            PieceType.S -> panel.theme.sColor
            PieceType.Z -> panel.theme.zColor
            PieceType.EMPTY -> panel.theme.backgroundColor
        }
    }

    fun allWindowsMac() {
        val isDark = panel.theme.backgroundColor == Color.BLACK
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