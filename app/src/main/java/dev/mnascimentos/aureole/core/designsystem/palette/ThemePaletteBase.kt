@file:Suppress("LargeClass", "TooManyFunctions", "FileLength", "MagicNumber", "MatchingDeclarationName")
// Suppressed detekt rules because this file serves as the unified Design System color palette registry.

package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

data class ThemePalette(
    val name: String,
    val colors: AureoleColors,
)

val frostbitePalette = ThemePalette(
    name = "Frostbite",
    colors = AureoleColors(
        onSurfaceHigh = Color(0xFFFFFFFF),
        onSurfaceMedium = Color(0xFFC5D8E8),
        onSurfaceLow = Color(0xFF9BB4C8),
        outline = Color(0xFF7D96AA),
        surfaceVariant = Color(0xFF4A5D6B),
        surface = Color(0xFF2B373E),
        background = Color(0xFF12181D),
    )
)

val allPalettes = listOf(
    frostbitePalette,
    ThemePalette(
        name = "Sunshine",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF9E6),
            onSurfaceMedium = Color(0xFFF2DCA6),
            onSurfaceLow = Color(0xFFC2A87A),
            outline = Color(0xFF947A52),
            surfaceVariant = Color(0xFF665133),
            surface = Color(0xFF3D2F1C),
            background = Color(0xFF1F170E),
        )
    ),
    ThemePalette(
        name = "Bubblegum",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFCE4EC),
            onSurfaceMedium = Color(0xFFF48FB1),
            onSurfaceLow = Color(0xFFC2185B),
            outline = Color(0xFF880E4F),
            surfaceVariant = Color(0xFF4A148C),
            surface = Color(0xFF311B92),
            background = Color(0xFF1A0A2E),
        )
    ),
    ThemePalette(
        name = "Mint",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0F7FA),
            onSurfaceMedium = Color(0xFF80DEEA),
            onSurfaceLow = Color(0xFF26C6DA),
            outline = Color(0xFF00ACC1),
            surfaceVariant = Color(0xFF00838F),
            surface = Color(0xFF006064),
            background = Color(0xFF00363A),
        )
    ),
    ThemePalette(
        name = "Citrus",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF8E1),
            onSurfaceMedium = Color(0xFFFFECB3),
            onSurfaceLow = Color(0xFFFFD54F),
            outline = Color(0xFFFFB300),
            surfaceVariant = Color(0xFFFF8F00),
            surface = Color(0xFFE65100),
            background = Color(0xFF3E2723),
        )
    ),
    ThemePalette(
        name = "Coral Reef",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFBE9E7),
            onSurfaceMedium = Color(0xFFFFAB91),
            onSurfaceLow = Color(0xFFFF7043),
            outline = Color(0xFFF4511E),
            surfaceVariant = Color(0xFFD84315),
            surface = Color(0xFFBF360C),
            background = Color(0xFF3E2723),
        )
    ),
    ThemePalette(
        name = "Berry",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFCE4EC),
            onSurfaceMedium = Color(0xFFF8BBD0),
            onSurfaceLow = Color(0xFFF06292),
            outline = Color(0xFFE91E63),
            surfaceVariant = Color(0xFFC2185B),
            surface = Color(0xFF880E4F),
            background = Color(0xFF4A148C),
        )
    ),
    ThemePalette(
        name = "Sky",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE1F5FE),
            onSurfaceMedium = Color(0xFF81D4FA),
            onSurfaceLow = Color(0xFF29B6F6),
            outline = Color(0xFF039BE5),
            surfaceVariant = Color(0xFF0277BD),
            surface = Color(0xFF01579B),
            background = Color(0xFF001F3F),
        )
    ),
    ThemePalette(
        name = "Lime",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF1F8E9),
            onSurfaceMedium = Color(0xFFC5E1A5),
            onSurfaceLow = Color(0xFF8BC34A),
            outline = Color(0xFF689F38),
            surfaceVariant = Color(0xFF33691E),
            surface = Color(0xFF1B5E20),
            background = Color(0xFF0A1C0B),
        )
    ),
    ThemePalette(
        name = "Lavender",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF3E5F5),
            onSurfaceMedium = Color(0xFFE1BEE7),
            onSurfaceLow = Color(0xFFCE93D8),
            outline = Color(0xFFAB47BC),
            surfaceVariant = Color(0xFF8E24AA),
            surface = Color(0xFF6A1B9A),
            background = Color(0xFF311B92),
        )
    ),
    ThemePalette(
        name = "Peach",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF3E0),
            onSurfaceMedium = Color(0xFFFFE0B2),
            onSurfaceLow = Color(0xFFFFCC80),
            outline = Color(0xFFFFA726),
            surfaceVariant = Color(0xFFFB8C00),
            surface = Color(0xFFEF6C00),
            background = Color(0xFF3E2723),
        )
    ),
    ThemePalette(
        name = "Aqua",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0F7FA),
            onSurfaceMedium = Color(0xFFB2EBF2),
            onSurfaceLow = Color(0xFF4DD0E1),
            outline = Color(0xFF00BCD4),
            surfaceVariant = Color(0xFF0097A7),
            surface = Color(0xFF006064),
            background = Color(0xFF00363A),
        )
    ),
    ThemePalette(
        name = "Gold",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFFDE7),
            onSurfaceMedium = Color(0xFFFFF9C4),
            onSurfaceLow = Color(0xFFFFF176),
            outline = Color(0xFFFDD835),
            surfaceVariant = Color(0xFFF9A825),
            surface = Color(0xFFF57F17),
            background = Color(0xFF3E2723),
        )
    ),
    ThemePalette(
        name = "Cherry Blossom",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF0F5),
            onSurfaceMedium = Color(0xFFFFD1DC),
            onSurfaceLow = Color(0xFFFFB6C1),
            outline = Color(0xFFFF69B4),
            surfaceVariant = Color(0xFFDB7093),
            surface = Color(0xFFC71585),
            background = Color(0xFF4A0E2E),
        )
    ),
    ThemePalette(
        name = "Dusty Rose",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5E6E8),
            onSurfaceMedium = Color(0xFFD4B5B9),
            onSurfaceLow = Color(0xFFA88B8F),
            outline = Color(0xFF806568),
            surfaceVariant = Color(0xFF584346),
            surface = Color(0xFF382A2C),
            background = Color(0xFF1C1415),
        )
    ),
    ThemePalette(
        name = "Sandstone",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5EDE6),
            onSurfaceMedium = Color(0xFFD4C0B0),
            onSurfaceLow = Color(0xFFA89585),
            outline = Color(0xFF806D5E),
            surfaceVariant = Color(0xFF58483D),
            surface = Color(0xFF382D25),
            background = Color(0xFF1C1612),
        )
    ),
    ThemePalette(
        name = "Storm",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFECEFF1),
            onSurfaceMedium = Color(0xFFB0BEC5),
            onSurfaceLow = Color(0xFF78909C),
            outline = Color(0xFF546E7A),
            surfaceVariant = Color(0xFF37474F),
            surface = Color(0xFF263238),
            background = Color(0xFF12181D),
        )
    ),
    ThemePalette(
        name = "Ash",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5F5F5),
            onSurfaceMedium = Color(0xFFE0E0E0),
            onSurfaceLow = Color(0xFF9E9E9E),
            outline = Color(0xFF757575),
            surfaceVariant = Color(0xFF424242),
            surface = Color(0xFF212121),
            background = Color(0xFF000000),
        )
    ),
    ThemePalette(
        name = "Dusk",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFEDE7F6),
            onSurfaceMedium = Color(0xFFD1C4E9),
            onSurfaceLow = Color(0xFFB39DDB),
            outline = Color(0xFF9575CD),
            surfaceVariant = Color(0xFF673AB7),
            surface = Color(0xFF512DA8),
            background = Color(0xFF311B92),
        )
    ),
    ThemePalette(
        name = "Slate",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFECEFF1),
            onSurfaceMedium = Color(0xFFCFD8DC),
            onSurfaceLow = Color(0xFF90A4AE),
            outline = Color(0xFF607D8B),
            surfaceVariant = Color(0xFF455A64),
            surface = Color(0xFF37474F),
            background = Color(0xFF263238),
        )
    ),
    ThemePalette(
        name = "Moss",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF1F8E9),
            onSurfaceMedium = Color(0xFFDCEDC8),
            onSurfaceLow = Color(0xFFAED581),
            outline = Color(0xFF7CB342),
            surfaceVariant = Color(0xFF558B2F),
            surface = Color(0xFF33691E),
            background = Color(0xFF1B5E20),
        )
    ),
    ThemePalette(
        name = "Wine",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8BBD0),
            onSurfaceMedium = Color(0xFFF06292),
            onSurfaceLow = Color(0xFFE91E63),
            outline = Color(0xFFC2185B),
            surfaceVariant = Color(0xFF880E4F),
            surface = Color(0xFF4A148C),
            background = Color(0xFF1A0A2E),
        )
    ),
    ThemePalette(
        name = "Navy",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE3F2FD),
            onSurfaceMedium = Color(0xFF90CAF9),
            onSurfaceLow = Color(0xFF42A5F5),
            outline = Color(0xFF1E88E5),
            surfaceVariant = Color(0xFF1565C0),
            surface = Color(0xFF0D47A1),
            background = Color(0xFF001F3F),
        )
    ),
    ThemePalette(
        name = "Charcoal",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFAFAFA),
            onSurfaceMedium = Color(0xFFE0E0E0),
            onSurfaceLow = Color(0xFF9E9E9E),
            outline = Color(0xFF616161),
            surfaceVariant = Color(0xFF424242),
            surface = Color(0xFF212121),
            background = Color(0xFF121212),
        )
    ),
    ThemePalette(
        name = "Fog",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFECEFF1),
            onSurfaceMedium = Color(0xFFCFD8DC),
            onSurfaceLow = Color(0xFFB0BEC5),
            outline = Color(0xFF90A4AE),
            surfaceVariant = Color(0xFF607D8B),
            surface = Color(0xFF455A64),
            background = Color(0xFF263238),
        )
    ),
    ThemePalette(
        name = "Drained",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5F5F5),
            onSurfaceMedium = Color(0xFFE0E0E0),
            onSurfaceLow = Color(0xFFBDBDBD),
            outline = Color(0xFF9E9E9E),
            surfaceVariant = Color(0xFF757575),
            surface = Color(0xFF424242),
            background = Color(0xFF212121),
        )
    ),
    ThemePalette(
        name = "Amethyst",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFEFE7F5),
            onSurfaceMedium = Color(0xFFD0C3E0),
            onSurfaceLow = Color(0xFFA89AB8),
            outline = Color(0xFF7D6E8A),
            surfaceVariant = Color(0xFF544A5E),
            surface = Color(0xFF312C36),
            background = Color(0xFF19151C),
        )
    ),
    ThemePalette(
        name = "Forest",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE3F2E9),
            onSurfaceMedium = Color(0xFFB8D8C4),
            onSurfaceLow = Color(0xFF8FAE9B),
            outline = Color(0xFF668B76),
            surfaceVariant = Color(0xFF405A4A),
            surface = Color(0xFF26362C),
            background = Color(0xFF121A15),
        )
    ),
    ThemePalette(
        name = "Crimson",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFCE8EA),
            onSurfaceMedium = Color(0xFFE8BCC1),
            onSurfaceLow = Color(0xFFB98C92),
            outline = Color(0xFF8C6167),
            surfaceVariant = Color(0xFF5E3E42),
            surface = Color(0xFF362326),
            background = Color(0xFF1A1012),
        )
    ),
    ThemePalette(
        name = "Earth",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF7EEE7),
            onSurfaceMedium = Color(0xFFE0C9B5),
            onSurfaceLow = Color(0xFFB59C87),
            outline = Color(0xFF87705E),
            surfaceVariant = Color(0xFF5A4739),
            surface = Color(0xFF35291F),
            background = Color(0xFF19140F),
        )
    ),
    ThemePalette(
        name = "Oceanic",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0F3F4),
            onSurfaceMedium = Color(0xFFB2D8DA),
            onSurfaceLow = Color(0xFF86ADB0),
            outline = Color(0xFF5C8689),
            surfaceVariant = Color(0xFF395758),
            surface = Color(0xFF213434),
            background = Color(0xFF101A1A),
        )
    ),
    ThemePalette(
        name = "Rose",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF9E8F0),
            onSurfaceMedium = Color(0xFFE4C0D2),
            onSurfaceLow = Color(0xFFB893A6),
            outline = Color(0xFF8B6478),
            surfaceVariant = Color(0xFF5C3F50),
            surface = Color(0xFF362430),
            background = Color(0xFF1A1017),
        )
    ),
    ThemePalette(
        name = "Olive",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5F3E6),
            onSurfaceMedium = Color(0xFFD6D2B5),
            onSurfaceLow = Color(0xFFA8A487),
            outline = Color(0xFF7D7A61),
            surfaceVariant = Color(0xFF514E3F),
            surface = Color(0xFF302E25),
            background = Color(0xFF171612),
        )
    ),
    ThemePalette(
        name = "Monochrome",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0F0F0),
            onSurfaceMedium = Color(0xFFD1D1D1),
            onSurfaceLow = Color(0xFFA3A3A3),
            outline = Color(0xFF787878),
            surfaceVariant = Color(0xFF4D4D4D),
            surface = Color(0xFF2E2E2E),
            background = Color(0xFF141414),
        )
    )
)

fun getPaletteByName(name: String): ThemePalette {
    return allPalettes.find { it.name.equals(name, ignoreCase = true) } ?: frostbitePalette
}
