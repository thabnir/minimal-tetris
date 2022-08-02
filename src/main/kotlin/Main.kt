import com.formdev.flatlaf.util.SystemInfo

fun main() {
    if (SystemInfo.isMacOS) {
        // enable screen menu bar [i think this is an instruction for where to enable it]
        // (moves menu bar from JFrame window to top of screen)
        System.setProperty("apple.laf.useScreenMenuBar", Theme.hasTransparentWindowBar.toString())

        // application name used in screen menu bar (in first menu after the "apple" menu)
        System.setProperty("apple.awt.application.name", "Tetris")

        // appearance of window title bars
        // possible values:
        //   - "system": use current macOS appearance (light or dark)
        //   - "NSAppearanceNameAqua": use light appearance
        //   - "NSAppearanceNameDarkAqua": use dark appearance
        // (needs to be set on main thread; setting it on AWT thread does not work)
        // if (Theme.isDarkMode) System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua")
        // else System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua")
    }

    val g = GraphicsPanel()
    g.run() // NOTE: DO NOT CALL RUN IN INITIALIZER (something to do with threads maybe? it won't work if you do)
}