@file:Suppress(
    "LargeClass",
    "TooManyFunctions",
    "FileLength",
    "MagicNumber",
    "MatchingDeclarationName",
    "unused"
)
// Suppressed detekt rules because this file serves as the unified Design System color palette registry.

package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

data class ThemePalette(
    val name: String,
    val colors: AureoleColors,
)

/**
 * Builds the light variant of a dark palette by mirroring the tonal ramp as in pallets.html:
 *   background      <- original onSurfaceHigh   (lightest)
 *   surface         <- original onSurfaceMedium
 *   surfaceVariant  <- original onSurfaceLow
 *   outline         <- original outline         (mid tone, unchanged)
 *   onSurfaceLow    <- original surfaceVariant
 *   onSurfaceMedium <- original surface
 *   onSurfaceHigh   <- original background      (darkest, primary text)
 */
fun ThemePalette.toLight(): ThemePalette = ThemePalette(
    name = "$name Light",
    colors = AureoleColors(
        onSurfaceHigh = colors.background,
        onSurfaceMedium = colors.surface,
        onSurfaceLow = colors.surfaceVariant,
        outline = colors.outline,
        surfaceVariant = colors.onSurfaceLow,
        surface = colors.onSurfaceMedium,
        background = colors.onSurfaceHigh,
    )
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

/**
 * Every base dark palette from pallets.html.
 */
val allPalettes = listOf(
    frostbitePalette,

    // ============ COLD ============
    ThemePalette(
        name = "Abyss",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0F7FA),
            onSurfaceMedium = Color(0xFF80DEEA),
            onSurfaceLow = Color(0xFF26C6DA),
            outline = Color(0xFF00838F),
            surfaceVariant = Color(0xFF006064),
            surface = Color(0xFF00363A),
            background = Color(0xFF001F20),
        )
    ),
    ThemePalette(
        name = "Tidepool",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFA2D7D5),
            onSurfaceMedium = Color(0xFF8BC5CD),
            onSurfaceLow = Color(0xFF55A3AB),
            outline = Color(0xFF297383),
            surfaceVariant = Color(0xFF1A343C),
            surface = Color(0xFF122228),
            background = Color(0xFF0A1418),
        )
    ),
    ThemePalette(
        name = "Midnight Fog",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8F8F2),
            onSurfaceMedium = Color(0xFF8BE9FD),
            onSurfaceLow = Color(0xFF6272A4),
            outline = Color(0xFF44475A),
            surfaceVariant = Color(0xFF282A36),
            surface = Color(0xFF1A1C25),
            background = Color(0xFF0D0E14),
        )
    ),
    ThemePalette(
        name = "Neon Dusk",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8F8F2),
            onSurfaceMedium = Color(0xFF66D9EF),
            onSurfaceLow = Color(0xFFAE81FF),
            outline = Color(0xFF49483E),
            surfaceVariant = Color(0xFF383830),
            surface = Color(0xFF272822),
            background = Color(0xFF1A1B17),
        )
    ),
    ThemePalette(
        name = "Phantom Blue",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF2FAEF),
            onSurfaceMedium = Color(0xFFA7DADC),
            onSurfaceLow = Color(0xFF447A9C),
            outline = Color(0xFF1D3658),
            surfaceVariant = Color(0xFF111F33),
            surface = Color(0xFF0A131F),
            background = Color(0xFF050A10),
        )
    ),
    ThemePalette(
        name = "Steel",
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
        name = "Glacier",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8F4F8),
            onSurfaceMedium = Color(0xFFB8DCE8),
            onSurfaceLow = Color(0xFF7BB8D0),
            outline = Color(0xFF4A8CA8),
            surfaceVariant = Color(0xFF2D5F78),
            surface = Color(0xFF1A3A4A),
            background = Color(0xFF0D1E26),
        )
    ),
    ThemePalette(
        name = "Arctic",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0F8FF),
            onSurfaceMedium = Color(0xFFD6EDF8),
            onSurfaceLow = Color(0xFFA8D4E8),
            outline = Color(0xFF6BA8C8),
            surfaceVariant = Color(0xFF3D7A9A),
            surface = Color(0xFF1F4A60),
            background = Color(0xFF0A2530),
        )
    ),
    ThemePalette(
        name = "Denim",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8EDF5),
            onSurfaceMedium = Color(0xFFC5D0E0),
            onSurfaceLow = Color(0xFF8FA3C0),
            outline = Color(0xFF5C7396),
            surfaceVariant = Color(0xFF3A4D6B),
            surface = Color(0xFF1F2D42),
            background = Color(0xFF0D1420),
        )
    ),
    ThemePalette(
        name = "Cobalt",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0E8FF),
            onSurfaceMedium = Color(0xFFB3C6F0),
            onSurfaceLow = Color(0xFF6B8ED6),
            outline = Color(0xFF3A5CB0),
            surfaceVariant = Color(0xFF1F3A7A),
            surface = Color(0xFF0D1F4A),
            background = Color(0xFF050D20),
        )
    ),
    ThemePalette(
        name = "Sapphire",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE3E8FF),
            onSurfaceMedium = Color(0xFFB8C4F0),
            onSurfaceLow = Color(0xFF7B8ED6),
            outline = Color(0xFF4A5CB0),
            surfaceVariant = Color(0xFF2A3A7A),
            surface = Color(0xFF15204A),
            background = Color(0xFF080D20),
        )
    ),
    ThemePalette(
        name = "Indigo",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8E0FF),
            onSurfaceMedium = Color(0xFFC4B3F0),
            onSurfaceLow = Color(0xFF8E6BD6),
            outline = Color(0xFF5C3AB0),
            surfaceVariant = Color(0xFF3A1F7A),
            surface = Color(0xFF200D4A),
            background = Color(0xFF0D0520),
        )
    ),
    ThemePalette(
        name = "Violet",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF3E5F5),
            onSurfaceMedium = Color(0xFFD6B8E8),
            onSurfaceLow = Color(0xFFB87BD0),
            outline = Color(0xFF8E4AA8),
            surfaceVariant = Color(0xFF5C2A70),
            surface = Color(0xFF351845),
            background = Color(0xFF1A0A20),
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

    // ============ COOL ============
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
        name = "Lilac",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5F0FA),
            onSurfaceMedium = Color(0xFFE0D0F0),
            onSurfaceLow = Color(0xFFC0A8E0),
            outline = Color(0xFF9A78C8),
            surfaceVariant = Color(0xFF6E4AA0),
            surface = Color(0xFF452878),
            background = Color(0xFF201040),
        )
    ),
    ThemePalette(
        name = "Wisteria",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0E8F8),
            onSurfaceMedium = Color(0xFFD8C0E8),
            onSurfaceLow = Color(0xFFB898D0),
            outline = Color(0xFF9070B0),
            surfaceVariant = Color(0xFF684A88),
            surface = Color(0xFF403060),
            background = Color(0xFF1C1428),
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
        name = "Seedling",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE3F2E9),
            onSurfaceMedium = Color(0xFFB8D8C4),
            onSurfaceLow = Color(0xFF89C893),
            outline = Color(0xFF49896F),
            surfaceVariant = Color(0xFF2D5A42),
            surface = Color(0xFF1A3628),
            background = Color(0xFF0D1A14),
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
        name = "Emerald",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8F8E8),
            onSurfaceMedium = Color(0xFFB8E8B8),
            onSurfaceLow = Color(0xFF6BD86B),
            outline = Color(0xFF2AB82A),
            surfaceVariant = Color(0xFF1A7A1A),
            surface = Color(0xFF0D4A0D),
            background = Color(0xFF052005),
        )
    ),
    ThemePalette(
        name = "Jade",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0F0E8),
            onSurfaceMedium = Color(0xFFA8D8C0),
            onSurfaceLow = Color(0xFF6BB898),
            outline = Color(0xFF3A8A6A),
            surfaceVariant = Color(0xFF1F5A42),
            surface = Color(0xFF0D3528),
            background = Color(0xFF051A14),
        )
    ),
    ThemePalette(
        name = "Sage",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFEEF2E8),
            onSurfaceMedium = Color(0xFFD0DCC0),
            onSurfaceLow = Color(0xFFA8B898),
            outline = Color(0xFF7A8E68),
            surfaceVariant = Color(0xFF4E6040),
            surface = Color(0xFF2E3A24),
            background = Color(0xFF141A0E),
        )
    ),
    ThemePalette(
        name = "Eucalyptus",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8F0EC),
            onSurfaceMedium = Color(0xFFC0D8CC),
            onSurfaceLow = Color(0xFF90B8A4),
            outline = Color(0xFF608878),
            surfaceVariant = Color(0xFF3A5A4E),
            surface = Color(0xFF1F3528),
            background = Color(0xFF0D1A14),
        )
    ),
    ThemePalette(
        name = "Teal",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0F2F0),
            onSurfaceMedium = Color(0xFFA8D8D0),
            onSurfaceLow = Color(0xFF6BB8B0),
            outline = Color(0xFF3A8A82),
            surfaceVariant = Color(0xFF1F5A54),
            surface = Color(0xFF0D352F),
            background = Color(0xFF051A18),
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
        name = "Twilight",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFABB2BF),
            onSurfaceMedium = Color(0xFF98C379),
            onSurfaceLow = Color(0xFF61AFEF),
            outline = Color(0xFF528BFF),
            surfaceVariant = Color(0xFF3E4451),
            surface = Color(0xFF282C34),
            background = Color(0xFF1B1F23),
        )
    ),
    ThemePalette(
        name = "Fjord",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFECEFF4),
            onSurfaceMedium = Color(0xFFD8DEE9),
            onSurfaceLow = Color(0xFF88C0D0),
            outline = Color(0xFF5E81AC),
            surfaceVariant = Color(0xFF4C566A),
            surface = Color(0xFF3B4252),
            background = Color(0xFF2E3440),
        )
    ),

    // ============ NEUTRAL ============
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
        name = "Periwinkle",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8E8F8),
            onSurfaceMedium = Color(0xFFC8C8E8),
            onSurfaceLow = Color(0xFFA0A0D0),
            outline = Color(0xFF7878B0),
            surfaceVariant = Color(0xFF505088),
            surface = Color(0xFF303060),
            background = Color(0xFF14143A),
        )
    ),
    ThemePalette(
        name = "Petal",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8E8F0),
            onSurfaceMedium = Color(0xFFE8C8D8),
            onSurfaceLow = Color(0xFFD0A0B8),
            outline = Color(0xFFB07890),
            surfaceVariant = Color(0xFF885868),
            surface = Color(0xFF5A3840),
            background = Color(0xFF28181E),
        )
    ),
    ThemePalette(
        name = "Orchid",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5E0F0),
            onSurfaceMedium = Color(0xFFE0B8D8),
            onSurfaceLow = Color(0xFFC888B8),
            outline = Color(0xFFA85890),
            surfaceVariant = Color(0xFF783868),
            surface = Color(0xFF4A2040),
            background = Color(0xFF201018),
        )
    ),
    ThemePalette(
        name = "Plum",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0E0E8),
            onSurfaceMedium = Color(0xFFD8B8C8),
            onSurfaceLow = Color(0xFFB888A0),
            outline = Color(0xFF8E5878),
            surfaceVariant = Color(0xFF603858),
            surface = Color(0xFF3A2030),
            background = Color(0xFF181018),
        )
    ),
    ThemePalette(
        name = "Eggplant",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8D8E0),
            onSurfaceMedium = Color(0xFFC8A8B8),
            onSurfaceLow = Color(0xFFA07890),
            outline = Color(0xFF784868),
            surfaceVariant = Color(0xFF502848),
            surface = Color(0xFF2E1430),
            background = Color(0xFF140818),
        )
    ),
    ThemePalette(
        name = "Grape",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8E0F0),
            onSurfaceMedium = Color(0xFFC8B8D8),
            onSurfaceLow = Color(0xFFA088B8),
            outline = Color(0xFF785898),
            surfaceVariant = Color(0xFF503878),
            surface = Color(0xFF2E1A50),
            background = Color(0xFF140828),
        )
    ),
    ThemePalette(
        name = "Mulberry",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8D8E8),
            onSurfaceMedium = Color(0xFFC8A8C8),
            onSurfaceLow = Color(0xFFA078A0),
            outline = Color(0xFF784878),
            surfaceVariant = Color(0xFF502850),
            surface = Color(0xFF2E1430),
            background = Color(0xFF140818),
        )
    ),
    ThemePalette(
        name = "Boysenberry",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0D0E0),
            onSurfaceMedium = Color(0xFFC0A0C0),
            onSurfaceLow = Color(0xFF987098),
            outline = Color(0xFF704870),
            surfaceVariant = Color(0xFF482848),
            surface = Color(0xFF281428),
            background = Color(0xFF100810),
        )
    ),
    ThemePalette(
        name = "Neon City",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8F8F2),
            onSurfaceMedium = Color(0xFFFF2A6D),
            onSurfaceLow = Color(0xFF05D9E8),
            outline = Color(0xFF7700FF),
            surfaceVariant = Color(0xFF1A0033),
            surface = Color(0xFF0D001A),
            background = Color(0xFF05000A),
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

    // ============ WARM ============
    ThemePalette(
        name = "Taupe",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0EAE8),
            onSurfaceMedium = Color(0xFFD8CCC8),
            onSurfaceLow = Color(0xFFB0A098),
            outline = Color(0xFF887870),
            surfaceVariant = Color(0xFF605048),
            surface = Color(0xFF3A3028),
            background = Color(0xFF1A1612),
        )
    ),
    ThemePalette(
        name = "Clay",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5E8E0),
            onSurfaceMedium = Color(0xFFE0C8B8),
            onSurfaceLow = Color(0xFFB8A090),
            outline = Color(0xFF907868),
            surfaceVariant = Color(0xFF685048),
            surface = Color(0xFF403028),
            background = Color(0xFF1C1410),
        )
    ),
    ThemePalette(
        name = "Bronze",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0E8D8),
            onSurfaceMedium = Color(0xFFD8C8A8),
            onSurfaceLow = Color(0xFFB8A078),
            outline = Color(0xFF907850),
            surfaceVariant = Color(0xFF685838),
            surface = Color(0xFF403020),
            background = Color(0xFF1C1408),
        )
    ),
    ThemePalette(
        name = "Copper",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8E8D8),
            onSurfaceMedium = Color(0xFFE8C8A8),
            onSurfaceLow = Color(0xFFC8A078),
            outline = Color(0xFFA07850),
            surfaceVariant = Color(0xFF785838),
            surface = Color(0xFF503820),
            background = Color(0xFF281808),
        )
    ),
    ThemePalette(
        name = "Driftwood",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0ECE8),
            onSurfaceMedium = Color(0xFFD8D0C8),
            onSurfaceLow = Color(0xFFB0A898),
            outline = Color(0xFF887868),
            surfaceVariant = Color(0xFF605048),
            surface = Color(0xFF3A3028),
            background = Color(0xFF1A1610),
        )
    ),
    ThemePalette(
        name = "Moss Stone",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8E8E0),
            onSurfaceMedium = Color(0xFFC8C8B8),
            onSurfaceLow = Color(0xFFA0A090),
            outline = Color(0xFF787868),
            surfaceVariant = Color(0xFF505048),
            surface = Color(0xFF2E2E28),
            background = Color(0xFF121210),
        )
    ),
    ThemePalette(
        name = "Mocha",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0E8E0),
            onSurfaceMedium = Color(0xFFD8C8B8),
            onSurfaceLow = Color(0xFFB8A090),
            outline = Color(0xFF907868),
            surfaceVariant = Color(0xFF685048),
            surface = Color(0xFF403028),
            background = Color(0xFF1C1410),
        )
    ),
    ThemePalette(
        name = "Latte",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF5F0E8),
            onSurfaceMedium = Color(0xFFE0D0C0),
            onSurfaceLow = Color(0xFFC0B0A0),
            outline = Color(0xFF988878),
            surfaceVariant = Color(0xFF706058),
            surface = Color(0xFF483828),
            background = Color(0xFF201810),
        )
    ),
    ThemePalette(
        name = "Caramel",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8E8D0),
            onSurfaceMedium = Color(0xFFE8D0B0),
            onSurfaceLow = Color(0xFFD0B090),
            outline = Color(0xFFB08868),
            surfaceVariant = Color(0xFF886848),
            surface = Color(0xFF604830),
            background = Color(0xFF302010),
        )
    ),
    ThemePalette(
        name = "Ember Tail",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFE0B2),
            onSurfaceMedium = Color(0xFFFFB74D),
            onSurfaceLow = Color(0xFFFF8A65),
            outline = Color(0xFFE64A19),
            surfaceVariant = Color(0xFFBF360C),
            surface = Color(0xFF7F2400),
            background = Color(0xFF3E1200),
        )
    ),
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
        name = "Round Pink",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF0F5),
            onSurfaceMedium = Color(0xFFFFD1DC),
            onSurfaceLow = Color(0xFFFFAABB),
            outline = Color(0xFFFF7F9F),
            surfaceVariant = Color(0xFFE05070),
            surface = Color(0xFFA02040),
            background = Color(0xFF400010),
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
        name = "Shadow Grin",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8D8F0),
            onSurfaceMedium = Color(0xFFC8A8D8),
            onSurfaceLow = Color(0xFFA878B8),
            outline = Color(0xFF885898),
            surfaceVariant = Color(0xFF583868),
            surface = Color(0xFF301840),
            background = Color(0xFF140820),
        )
    ),
    ThemePalette(
        name = "Volt Amber",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFEF5A3),
            onSurfaceMedium = Color(0xFFFAD61D),
            onSurfaceLow = Color(0xFFE19720),
            outline = Color(0xFFBF2923),
            surfaceVariant = Color(0xFF811E09),
            surface = Color(0xFF4A1206),
            background = Color(0xFF1F0802),
        )
    ),
    ThemePalette(
        name = "Phantom Red",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF2FAEF),
            onSurfaceMedium = Color(0xFFE63746),
            onSurfaceLow = Color(0xFFD92323),
            outline = Color(0xFF8C6723),
            surfaceVariant = Color(0xFF732424),
            surface = Color(0xFF3D1212),
            background = Color(0xFF1A0808),
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
        name = "Blush",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFCE8E8),
            onSurfaceMedium = Color(0xFFF0C0C0),
            onSurfaceLow = Color(0xFFE09090),
            outline = Color(0xFFC86060),
            surfaceVariant = Color(0xFFA04040),
            surface = Color(0xFF702828),
            background = Color(0xFF381010),
        )
    ),
    ThemePalette(
        name = "Magenta",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFCE0F0),
            onSurfaceMedium = Color(0xFFF0B0D8),
            onSurfaceLow = Color(0xFFE070B8),
            outline = Color(0xFFC83088),
            surfaceVariant = Color(0xFF901858),
            surface = Color(0xFF580830),
            background = Color(0xFF200410),
        )
    ),
    ThemePalette(
        name = "Fuchsia",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8D8F0),
            onSurfaceMedium = Color(0xFFE8A8D8),
            onSurfaceLow = Color(0xFFD070B8),
            outline = Color(0xFFB03890),
            surfaceVariant = Color(0xFF781868),
            surface = Color(0xFF480838),
            background = Color(0xFF180410),
        )
    ),
    ThemePalette(
        name = "Rose Gold",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8E8E0),
            onSurfaceMedium = Color(0xFFE8C8B8),
            onSurfaceLow = Color(0xFFD0A090),
            outline = Color(0xFFB87868),
            surfaceVariant = Color(0xFF905040),
            surface = Color(0xFF603028),
            background = Color(0xFF281410),
        )
    ),
    ThemePalette(
        name = "Sunset",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFE8D0),
            onSurfaceMedium = Color(0xFFFFC8A0),
            onSurfaceLow = Color(0xFFFF9870),
            outline = Color(0xFFF06040),
            surfaceVariant = Color(0xFFC03020),
            surface = Color(0xFF801810),
            background = Color(0xFF300808),
        )
    ),
    ThemePalette(
        name = "Amber",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF0D0),
            onSurfaceMedium = Color(0xFFFFD8A0),
            onSurfaceLow = Color(0xFFFFB870),
            outline = Color(0xFFF09040),
            surfaceVariant = Color(0xFFC06820),
            surface = Color(0xFF804010),
            background = Color(0xFF301808),
        )
    ),
    ThemePalette(
        name = "Honey",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF8E0),
            onSurfaceMedium = Color(0xFFFFE8B0),
            onSurfaceLow = Color(0xFFFFD080),
            outline = Color(0xFFF0B050),
            surfaceVariant = Color(0xFFC08830),
            surface = Color(0xFF805818),
            background = Color(0xFF302008),
        )
    ),
    ThemePalette(
        name = "Marigold",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFF8D0),
            onSurfaceMedium = Color(0xFFFFE8A0),
            onSurfaceLow = Color(0xFFFFD060),
            outline = Color(0xFFF0B030),
            surfaceVariant = Color(0xFFC08018),
            surface = Color(0xFF805008),
            background = Color(0xFF301808),
        )
    ),
    ThemePalette(
        name = "Tangerine",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFE8D0),
            onSurfaceMedium = Color(0xFFFFC8A0),
            onSurfaceLow = Color(0xFFFFA070),
            outline = Color(0xFFF07840),
            surfaceVariant = Color(0xFFC05020),
            surface = Color(0xFF803010),
            background = Color(0xFF301008),
        )
    ),
    ThemePalette(
        name = "Papaya",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFE0C0),
            onSurfaceMedium = Color(0xFFFFC090),
            onSurfaceLow = Color(0xFFFFA060),
            outline = Color(0xFFF07830),
            surfaceVariant = Color(0xFFC05018),
            surface = Color(0xFF802808),
            background = Color(0xFF300C08),
        )
    ),
    ThemePalette(
        name = "Flamingo",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFD8D8),
            onSurfaceMedium = Color(0xFFFFB0B0),
            onSurfaceLow = Color(0xFFFF8080),
            outline = Color(0xFFF05050),
            surfaceVariant = Color(0xFFC02828),
            surface = Color(0xFF801010),
            background = Color(0xFF300808),
        )
    ),
    ThemePalette(
        name = "Salmon",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFE0D0),
            onSurfaceMedium = Color(0xFFFFC0A8),
            onSurfaceLow = Color(0xFFFFA080),
            outline = Color(0xFFF07858),
            surfaceVariant = Color(0xFFC05038),
            surface = Color(0xFF802818),
            background = Color(0xFF300C08),
        )
    ),
    ThemePalette(
        name = "Melon",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFE8D8),
            onSurfaceMedium = Color(0xFFFFD0B0),
            onSurfaceLow = Color(0xFFFFB888),
            outline = Color(0xFFF09060),
            surfaceVariant = Color(0xFFC06838),
            surface = Color(0xFF803818),
            background = Color(0xFF301408),
        )
    ),
    ThemePalette(
        name = "Watermelon",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFD8E0),
            onSurfaceMedium = Color(0xFFFFB0C0),
            onSurfaceLow = Color(0xFFFF7890),
            outline = Color(0xFFF04860),
            surfaceVariant = Color(0xFFC02038),
            surface = Color(0xFF800818),
            background = Color(0xFF300408),
        )
    ),
    ThemePalette(
        name = "Strawberry",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFD8D8),
            onSurfaceMedium = Color(0xFFFFA8A8),
            onSurfaceLow = Color(0xFFFF7070),
            outline = Color(0xFFF03838),
            surfaceVariant = Color(0xFFC01818),
            surface = Color(0xFF800808),
            background = Color(0xFF300000),
        )
    ),
    ThemePalette(
        name = "Raspberry",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8D0D8),
            onSurfaceMedium = Color(0xFFE8A0B0),
            onSurfaceLow = Color(0xFFD07088),
            outline = Color(0xFFB84060),
            surfaceVariant = Color(0xFF882038),
            surface = Color(0xFF501020),
            background = Color(0xFF180408),
        )
    ),
    ThemePalette(
        name = "Cherry",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF8D0D0),
            onSurfaceMedium = Color(0xFFE8A0A0),
            onSurfaceLow = Color(0xFFD07070),
            outline = Color(0xFFB84040),
            surfaceVariant = Color(0xFF882020),
            surface = Color(0xFF501010),
            background = Color(0xFF180000),
        )
    ),
    ThemePalette(
        name = "Poppy",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFFFD8D0),
            onSurfaceMedium = Color(0xFFFFB0A0),
            onSurfaceLow = Color(0xFFFF8870),
            outline = Color(0xFFF05840),
            surfaceVariant = Color(0xFFC03020),
            surface = Color(0xFF801810),
            background = Color(0xFF300808),
        )
    ),
    ThemePalette(
        name = "Brick",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8D0C0),
            onSurfaceMedium = Color(0xFFD0A890),
            onSurfaceLow = Color(0xFFB88060),
            outline = Color(0xFF986040),
            surfaceVariant = Color(0xFF704028),
            surface = Color(0xFF482818),
            background = Color(0xFF180C08),
        )
    ),
    ThemePalette(
        name = "Rust",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8D0B8),
            onSurfaceMedium = Color(0xFFD0A888),
            onSurfaceLow = Color(0xFFB88058),
            outline = Color(0xFF986038),
            surfaceVariant = Color(0xFF704020),
            surface = Color(0xFF482810),
            background = Color(0xFF180C08),
        )
    ),
    ThemePalette(
        name = "Terracotta",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFF0D8C8),
            onSurfaceMedium = Color(0xFFD8B098),
            onSurfaceLow = Color(0xFFC08868),
            outline = Color(0xFFA06040),
            surfaceVariant = Color(0xFF784028),
            surface = Color(0xFF503018),
            background = Color(0xFF201008),
        )
    ),
    ThemePalette(
        name = "Sienna",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE8D0B8),
            onSurfaceMedium = Color(0xFFD0A880),
            onSurfaceLow = Color(0xFFB88050),
            outline = Color(0xFF986028),
            surfaceVariant = Color(0xFF704010),
            surface = Color(0xFF482808),
            background = Color(0xFF180C00),
        )
    ),
    ThemePalette(
        name = "Mahogany",
        colors = AureoleColors(
            onSurfaceHigh = Color(0xFFE0C8B8),
            onSurfaceMedium = Color(0xFFC8A088),
            onSurfaceLow = Color(0xFFA87858),
            outline = Color(0xFF885838),
            surfaceVariant = Color(0xFF603820),
            surface = Color(0xFF381810),
            background = Color(0xFF140808),
        )
    )
)

/**
 * Creates a dynamic [ThemePalette] from a seed ARGB color int.
 */
fun createPaletteFromSeedColor(seedColorInt: Int, isDarkTheme: Boolean = true): ThemePalette {
    val colors = buildSeedAureoleColors(seedColorInt)
    return ThemePalette(
        name = if (isDarkTheme) "Custom" else "Custom Light",
        colors = colors
    )
}

private fun buildSeedAureoleColors(seedColorInt: Int): AureoleColors {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(seedColorInt, hsl)
    val hue = hsl[0]
    val sat = hsl[1]
    val isDarkBg = ColorUtils.calculateLuminance(seedColorInt) < 0.5f

    val offset = if (isDarkBg) 1f else -1f
    fun adjustLightness(delta: Float) = (hsl[2] + offset * delta).coerceIn(0f, 1f)

    val surfaceColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(0.08f))))
    val surfaceVariantColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(0.16f))))
    val outlineColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(0.28f))))

    val onHigh = if (isDarkBg) Color(0xFFFFFFFF) else Color(0xFF101418)
    val onMedium =
        Color(ColorUtils.HSLToColor(floatArrayOf(hue, (sat * 0.3f).coerceIn(0f, 1f), if (isDarkBg) 0.82f else 0.22f)))
    val onLow =
        Color(ColorUtils.HSLToColor(floatArrayOf(hue, (sat * 0.3f).coerceIn(0f, 1f), if (isDarkBg) 0.65f else 0.38f)))

    return AureoleColors(
        background = Color(seedColorInt),
        surface = surfaceColor,
        surfaceVariant = surfaceVariantColor,
        outline = outlineColor,
        onSurfaceLow = onLow,
        onSurfaceMedium = onMedium,
        onSurfaceHigh = onHigh
    )
}

/**
 * Returns the theme palette corresponding to [name].
 * If [isDarkTheme] is true, returns the dark variant; otherwise returns the light variant.
 */
fun getPaletteByName(
    name: String,
    isDarkTheme: Boolean = true,
    seedColor: Int? = null
): ThemePalette {
    val cleanName = name.removeSuffix(" Light").trim()
    if (cleanName.equals("Custom", ignoreCase = true)) {
        return createPaletteFromSeedColor(
            seedColor ?: SettingsRepository.DEFAULT_SEED_COLOR,
            isDarkTheme
        )
    }
    val darkPalette =
        allPalettes.find { it.name.equals(cleanName, ignoreCase = true) } ?: frostbitePalette
    return if (isDarkTheme) darkPalette else darkPalette.toLight()
}

/**
 * Returns the light variant of the palette corresponding to [name].
 */
fun getLightPaletteByName(name: String, seedColor: Int? = null): ThemePalette {
    return getPaletteByName(name, isDarkTheme = false, seedColor = seedColor)
}
