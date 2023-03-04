import java.awt.Color


enum class Theme(
    val printableName: String,
    private val lightBackgroundColor: Color,
    private val darkBackgroundColor: Color,
    private val lightGhostPieceAlpha: Int,
    private val darkGhostPieceAlpha: Int,
    private val lightLColor: Color,
    private val lightJColor: Color,
    private val lightIColor: Color,
    private val lightTColor: Color,
    private val lightOColor: Color,
    private val lightSColor: Color,
    private val lightZColor: Color,
    private val darkLColor: Color,
    private val darkJColor: Color,
    private val darkIColor: Color,
    private val darkTColor: Color,
    private val darkOColor: Color,
    private val darkSColor: Color,
    private val darkZColor: Color
) {
    BABY(
        "Baby",
        Color(0xF9EAE1),
        Color(12, 12, 12),
        45,
        45,
        // light theme piece colors
        Color(130, 186, 255), // L Piece
        Color(255, 134, 213), // J Piece
        Color(151, 155, 253), // I Piece
        Color(172, 126, 255), // T Piece
        Color(255, 171, 245), // O Piece
        Color(116, 158, 255), // S Piece
        Color(255, 100, 170), // Z Piece
        // dark theme piece colors
        Color(130, 186, 255), // L Piece
        Color(255, 134, 213), // J Piece
        Color(151, 155, 253), // I Piece
        Color(172, 126, 255), // T Piece
        Color(255, 171, 245), // O Piece
        Color(116, 158, 255), // S Piece
        Color(255, 100, 170)  // Z Piece
    ),
    BLACK_AND_WHITE(
        "Black & White",
        Color.WHITE,
        Color.BLACK,
        45,
        45,
        // light theme piece colors
        Color.BLACK, // L Piece
        Color.BLACK, // J Piece
        Color.BLACK, // I Piece
        Color.BLACK, // T Piece
        Color.BLACK, // O Piece
        Color.BLACK, // S Piece
        Color.BLACK, // Z Piece
        // dark theme piece colors
        Color.WHITE, // L Piece
        Color.WHITE, // J Piece
        Color.WHITE, // I Piece
        Color.WHITE, // T Piece
        Color.WHITE, // O Piece
        Color.WHITE, // S Piece
        Color.WHITE  // Z Piece
    ),
    RETRO(
        "Retro",
        Color(0xFFFAF0),
        Color(0x1C1C1C),
        45,
        45,
        Color(0xF79F79),
        Color(0xF7B882),
        Color(0xF7D08A),
        Color(0xE3F09B),
        Color(0xBFD3A1),
        Color(0x87B6A7),
        Color(0x5B5941),
        Color(0xF79F79),
        Color(0xF7B882),
        Color(0xF7D08A),
        Color(0xE3F09B),
        Color(0xBFD3A1),
        Color(0x87B6A7),
        Color(0x5B5941),
    ),

    PASTEL(
        "Pastel",
        Color(0xF9EAE1),
        Color(0x1C1C1C),
        45,
        45,
        Color(0x9B5DE5),
        Color(0xF15BB5),
        Color(0xFEE440),
        Color(0x00BBF9),
        Color(0x00F5D4),
        Color(0x8338EC),
        Color(0xFF006E),
        Color(0x9B5DE5),
        Color(0xF15BB5),
        Color(0xFEE440),
        Color(0x00BBF9),
        Color(0x00F5D4),
        Color(0x8338EC),
        Color(0xFF006E)
    ),
    DISCO(
        "Disco",
        Color(0xE7E7E7),
        Color(12, 12, 12),
        45,
        45,
        Color(0xFF0000),
        Color(0xFF7F00),
        Color(0xFFFF00),
        Color(0x00FF00),
        Color(0x0000FF),
        Color(0x4B0082),
        Color(0x9400D3),
        Color(0xFF0000),
        Color(0xFF7F00),
        Color(0xFFFF00),
        Color(0x00FF00),
        Color(0x0000FF),
        Color(0x4B0082),
        Color(0x9400D3),
    ),
    COCAINE(
        "Cocaine",
        Color(0xFFFF33),
        Color(0xFF10F0),
        45,
        45,
        Color(0xC9DAEA),
        Color(0x03F7EB),
        Color(0x00B295),
        Color(0x0D6456),
        Color(0x191516),
        Color(0x621C2E),
        Color(0xAB2346),
        Color(0xC9DAEA),
        Color(0x03F7EB),
        Color(0x00B295),
        Color(0x0D6456),
        Color(0x191516),
        Color(0x621C2E),
        Color(0xAB2346),
    ),
    ;

    companion object {
        val hasTransparentWindowBar = true
        var isDarkMode: Boolean = true
        var isWireframeGhostPiece = true // breaks at gap == 0
            get() = if (gap == 0) false else field
        var bevelPercent = .25f // 0 to 1
        var gap = 2 // gap between cells
        var borderThickness = 1 // ghost piece line thickness (if it's an outline)
        var isRound = true
            get() = if (bevelPercent == 0f) false else field
    }

    val lColor: Color
        get() = if (isDarkMode) darkLColor else lightLColor
    val jColor: Color
        get() = if (isDarkMode) darkJColor else lightJColor
    val iColor: Color
        get() = if (isDarkMode) darkIColor else lightIColor
    val tColor: Color
        get() = if (isDarkMode) darkTColor else lightTColor
    val oColor: Color
        get() = if (isDarkMode) darkOColor else lightOColor
    val sColor: Color
        get() = if (isDarkMode) darkSColor else lightSColor
    val zColor: Color
        get() = if (isDarkMode) darkZColor else lightZColor
    val backgroundColor: Color
        get() = if (isDarkMode) darkBackgroundColor else lightBackgroundColor
    val ghostPieceAlpha: Int
        get() = if (!isWireframeGhostPiece) {
            if (isDarkMode) darkGhostPieceAlpha else lightGhostPieceAlpha
        } else {
            255
        }
}