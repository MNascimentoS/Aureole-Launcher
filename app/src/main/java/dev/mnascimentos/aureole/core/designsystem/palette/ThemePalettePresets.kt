package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

private const val HEX_RADIX = 16
private const val OPAQUE_ARGB = 0xFF000000L

/**
 * Builds a preset palette from [hex]: seven RGB values (no alpha) separated by spaces, in this order:
 * onSurfaceHigh, onSurfaceMedium, onSurfaceLow, outline, surfaceVariant, surface, background. All are opaque.
 */
internal fun presetPalette(name: String, category: PaletteCategory, hex: String): ThemePalette {
    val colors = hex.split(' ').map { Color(OPAQUE_ARGB or it.toLong(HEX_RADIX)) }
    return ThemePalette(
        name = name,
        category = category,
        colors = AureoleColors(
            onSurfaceHigh = colors[0],
            onSurfaceMedium = colors[1],
            onSurfaceLow = colors[2],
            outline = colors[3],
            surfaceVariant = colors[4],
            surface = colors[5],
            background = colors[6]
        )
    )
}

internal val presetPalettes: List<ThemePalette> = listOf(
    // ============ COLD ============
    presetPalette("Abyss", PaletteCategory.COLD, "E0F7FA 80DEEA 26C6DA 00838F 006064 00363A 001F20"),
    presetPalette("Tidepool", PaletteCategory.COLD, "A2D7D5 8BC5CD 55A3AB 297383 1A343C 122228 0A1418"),
    presetPalette("Midnight Fog", PaletteCategory.COLD, "F8F8F2 8BE9FD 6272A4 44475A 282A36 1A1C25 0D0E14"),
    presetPalette("Neon Dusk", PaletteCategory.COLD, "F8F8F2 66D9EF AE81FF 49483E 383830 272822 1A1B17"),
    presetPalette("Phantom Blue", PaletteCategory.COLD, "F2FAEF A7DADC 447A9C 1D3658 111F33 0A131F 050A10"),
    presetPalette("Steel", PaletteCategory.COLD, "ECEFF1 CFD8DC 90A4AE 607D8B 455A64 37474F 263238"),
    presetPalette("Sky", PaletteCategory.COLD, "E1F5FE 81D4FA 29B6F6 039BE5 0277BD 01579B 001F3F"),
    presetPalette("Navy", PaletteCategory.COLD, "E3F2FD 90CAF9 42A5F5 1E88E5 1565C0 0D47A1 001F3F"),
    presetPalette("Oceanic", PaletteCategory.COLD, "E0F3F4 B2D8DA 86ADB0 5C8689 395758 213434 101A1A"),
    presetPalette("Aqua", PaletteCategory.COLD, "E0F7FA B2EBF2 4DD0E1 00BCD4 0097A7 006064 00363A"),
    presetPalette("Storm", PaletteCategory.COLD, "ECEFF1 B0BEC5 78909C 546E7A 37474F 263238 12181D"),
    presetPalette("Fog", PaletteCategory.COLD, "ECEFF1 CFD8DC B0BEC5 90A4AE 607D8B 455A64 263238"),
    presetPalette("Slate", PaletteCategory.COLD, "ECEFF1 CFD8DC 90A4AE 607D8B 455A64 37474F 263238"),
    presetPalette("Glacier", PaletteCategory.COLD, "E8F4F8 B8DCE8 7BB8D0 4A8CA8 2D5F78 1A3A4A 0D1E26"),
    presetPalette("Arctic", PaletteCategory.COLD, "F0F8FF D6EDF8 A8D4E8 6BA8C8 3D7A9A 1F4A60 0A2530"),
    presetPalette("Denim", PaletteCategory.COLD, "E8EDF5 C5D0E0 8FA3C0 5C7396 3A4D6B 1F2D42 0D1420"),
    presetPalette("Cobalt", PaletteCategory.COLD, "E0E8FF B3C6F0 6B8ED6 3A5CB0 1F3A7A 0D1F4A 050D20"),
    presetPalette("Sapphire", PaletteCategory.COLD, "E3E8FF B8C4F0 7B8ED6 4A5CB0 2A3A7A 15204A 080D20"),
    presetPalette("Indigo", PaletteCategory.COLD, "E8E0FF C4B3F0 8E6BD6 5C3AB0 3A1F7A 200D4A 0D0520"),
    presetPalette("Violet", PaletteCategory.COLD, "F3E5F5 D6B8E8 B87BD0 8E4AA8 5C2A70 351845 1A0A20"),
    presetPalette("Amethyst", PaletteCategory.COLD, "EFE7F5 D0C3E0 A89AB8 7D6E8A 544A5E 312C36 19151C"),
    // ============ COOL ============
    presetPalette("Lavender", PaletteCategory.COOL, "F3E5F5 E1BEE7 CE93D8 AB47BC 8E24AA 6A1B9A 311B92"),
    presetPalette("Lilac", PaletteCategory.COOL, "F5F0FA E0D0F0 C0A8E0 9A78C8 6E4AA0 452878 201040"),
    presetPalette("Wisteria", PaletteCategory.COOL, "F0E8F8 D8C0E8 B898D0 9070B0 684A88 403060 1C1428"),
    presetPalette("Mint", PaletteCategory.COOL, "E0F7FA 80DEEA 26C6DA 00ACC1 00838F 006064 00363A"),
    presetPalette("Seedling", PaletteCategory.COOL, "E3F2E9 B8D8C4 89C893 49896F 2D5A42 1A3628 0D1A14"),
    presetPalette("Forest", PaletteCategory.COOL, "E3F2E9 B8D8C4 8FAE9B 668B76 405A4A 26362C 121A15"),
    presetPalette("Moss", PaletteCategory.COOL, "F1F8E9 DCEDC8 AED581 7CB342 558B2F 33691E 1B5E20"),
    presetPalette("Emerald", PaletteCategory.COOL, "E8F8E8 B8E8B8 6BD86B 2AB82A 1A7A1A 0D4A0D 052005"),
    presetPalette("Jade", PaletteCategory.COOL, "E0F0E8 A8D8C0 6BB898 3A8A6A 1F5A42 0D3528 051A14"),
    presetPalette("Sage", PaletteCategory.COOL, "EEF2E8 D0DCC0 A8B898 7A8E68 4E6040 2E3A24 141A0E"),
    presetPalette("Eucalyptus", PaletteCategory.COOL, "E8F0EC C0D8CC 90B8A4 608878 3A5A4E 1F3528 0D1A14"),
    presetPalette("Teal", PaletteCategory.COOL, "E0F2F0 A8D8D0 6BB8B0 3A8A82 1F5A54 0D352F 051A18"),
    presetPalette("Monochrome", PaletteCategory.COOL, "F0F0F0 D1D1D1 A3A3A3 787878 4D4D4D 2E2E2E 141414"),
    presetPalette("Ash", PaletteCategory.COOL, "F5F5F5 E0E0E0 9E9E9E 757575 424242 212121 000000"),
    presetPalette("Charcoal", PaletteCategory.COOL, "FAFAFA E0E0E0 9E9E9E 616161 424242 212121 121212"),
    presetPalette("Drained", PaletteCategory.COOL, "F5F5F5 E0E0E0 BDBDBD 9E9E9E 757575 424242 212121"),
    presetPalette("Twilight", PaletteCategory.COOL, "ABB2BF 98C379 61AFEF 528BFF 3E4451 282C34 1B1F23"),
    presetPalette("Fjord", PaletteCategory.COOL, "ECEFF4 D8DEE9 88C0D0 5E81AC 4C566A 3B4252 2E3440"),
    // ============ NEUTRAL ============
    presetPalette("Dusk", PaletteCategory.NEUTRAL, "EDE7F6 D1C4E9 B39DDB 9575CD 673AB7 512DA8 311B92"),
    presetPalette("Periwinkle", PaletteCategory.NEUTRAL, "E8E8F8 C8C8E8 A0A0D0 7878B0 505088 303060 14143A"),
    presetPalette("Petal", PaletteCategory.NEUTRAL, "F8E8F0 E8C8D8 D0A0B8 B07890 885868 5A3840 28181E"),
    presetPalette("Orchid", PaletteCategory.NEUTRAL, "F5E0F0 E0B8D8 C888B8 A85890 783868 4A2040 201018"),
    presetPalette("Plum", PaletteCategory.NEUTRAL, "F0E0E8 D8B8C8 B888A0 8E5878 603858 3A2030 181018"),
    presetPalette("Grape", PaletteCategory.NEUTRAL, "E8E0F0 C8B8D8 A088B8 785898 503878 2E1A50 140828"),
    presetPalette("Mulberry", PaletteCategory.NEUTRAL, "E8D8E8 C8A8C8 A078A0 784878 502850 2E1430 140818"),
    presetPalette("Boysenberry", PaletteCategory.NEUTRAL, "E0D0E0 C0A0C0 987098 704870 482848 281428 100810"),
    presetPalette("Neon City", PaletteCategory.NEUTRAL, "F8F8F2 FF2A6D 05D9E8 7700FF 1A0033 0D001A 05000A"),
    presetPalette("Sandstone", PaletteCategory.NEUTRAL, "F5EDE6 D4C0B0 A89585 806D5E 58483D 382D25 1C1612"),
    presetPalette("Earth", PaletteCategory.NEUTRAL, "F7EEE7 E0C9B5 B59C87 87705E 5A4739 35291F 19140F"),
    presetPalette("Olive", PaletteCategory.NEUTRAL, "F5F3E6 D6D2B5 A8A487 7D7A61 514E3F 302E25 171612"),
    presetPalette("Dusty Rose", PaletteCategory.NEUTRAL, "F5E6E8 D4B5B9 A88B8F 806568 584346 382A2C 1C1415"),
    presetPalette("Rose", PaletteCategory.NEUTRAL, "F9E8F0 E4C0D2 B893A6 8B6478 5C3F50 362430 1A1017"),
    // ============ WARM ============
    presetPalette("Taupe", PaletteCategory.WARM, "F0EAE8 D8CCC8 B0A098 887870 605048 3A3028 1A1612"),
    presetPalette("Clay", PaletteCategory.WARM, "F5E8E0 E0C8B8 B8A090 907868 685048 403028 1C1410"),
    presetPalette("Bronze", PaletteCategory.WARM, "F0E8D8 D8C8A8 B8A078 907850 685838 403020 1C1408"),
    presetPalette("Copper", PaletteCategory.WARM, "F8E8D8 E8C8A8 C8A078 A07850 785838 503820 281808"),
    presetPalette("Driftwood", PaletteCategory.WARM, "F0ECE8 D8D0C8 B0A898 887868 605048 3A3028 1A1610"),
    presetPalette("Moss Stone", PaletteCategory.WARM, "E8E8E0 C8C8B8 A0A090 787868 505048 2E2E28 121210"),
    presetPalette("Mocha", PaletteCategory.WARM, "F0E8E0 D8C8B8 B8A090 907868 685048 403028 1C1410"),
    presetPalette("Latte", PaletteCategory.WARM, "F5F0E8 E0D0C0 C0B0A0 988878 706058 483828 201810"),
    presetPalette("Caramel", PaletteCategory.WARM, "F8E8D0 E8D0B0 D0B090 B08868 886848 604830 302010"),
    presetPalette("Ember Tail", PaletteCategory.WARM, "FFE0B2 FFB74D FF8A65 E64A19 BF360C 7F2400 3E1200"),
    presetPalette("Sunshine", PaletteCategory.WARM, "FFF9E6 F2DCA6 C2A87A 947A52 665133 3D2F1C 1F170E"),
    presetPalette("Gold", PaletteCategory.WARM, "FFFDE7 FFF9C4 FFF176 FDD835 F9A825 F57F17 3E2723"),
    presetPalette("Citrus", PaletteCategory.WARM, "FFF8E1 FFECB3 FFD54F FFB300 FF8F00 E65100 3E2723"),
    presetPalette("Peach", PaletteCategory.WARM, "FFF3E0 FFE0B2 FFCC80 FFA726 FB8C00 EF6C00 3E2723"),
    presetPalette("Coral Reef", PaletteCategory.WARM, "FBE9E7 FFAB91 FF7043 F4511E D84315 BF360C 3E2723"),
    presetPalette("Cherry Blossom", PaletteCategory.WARM, "FFF0F5 FFD1DC FFB6C1 FF69B4 DB7093 C71585 4A0E2E"),
    presetPalette("Bubblegum", PaletteCategory.WARM, "FCE4EC F48FB1 C2185B 880E4F 4A148C 311B92 1A0A2E"),
    presetPalette("Berry", PaletteCategory.WARM, "FCE4EC F8BBD0 F06292 E91E63 C2185B 880E4F 4A148C"),
    presetPalette("Round Pink", PaletteCategory.WARM, "FFF0F5 FFD1DC FFAABB FF7F9F E05070 A02040 400010"),
    presetPalette("Crimson", PaletteCategory.WARM, "FCE8EA E8BCC1 B98C92 8C6167 5E3E42 362326 1A1012"),
    presetPalette("Shadow Grin", PaletteCategory.WARM, "E8D8F0 C8A8D8 A878B8 885898 583868 301840 140820"),
    presetPalette("Volt Amber", PaletteCategory.WARM, "FEF5A3 FAD61D E19720 BF2923 811E09 4A1206 1F0802"),
    presetPalette("Phantom Red", PaletteCategory.WARM, "F2FAEF E63746 D92323 8C6723 732424 3D1212 1A0808"),
    presetPalette("Wine", PaletteCategory.WARM, "F8BBD0 F06292 E91E63 C2185B 880E4F 4A148C 1A0A2E"),
    presetPalette("Blush", PaletteCategory.WARM, "FCE8E8 F0C0C0 E09090 C86060 A04040 702828 381010"),
    presetPalette("Magenta", PaletteCategory.WARM, "FCE0F0 F0B0D8 E070B8 C83088 901858 580830 200410"),
    presetPalette("Fuchsia", PaletteCategory.WARM, "F8D8F0 E8A8D8 D070B8 B03890 781868 480838 180410"),
    presetPalette("Rose Gold", PaletteCategory.WARM, "F8E8E0 E8C8B8 D0A090 B87868 905040 603028 281410"),
    presetPalette("Sunset", PaletteCategory.WARM, "FFE8D0 FFC8A0 FF9870 F06040 C03020 801810 300808"),
    presetPalette("Amber", PaletteCategory.WARM, "FFF0D0 FFD8A0 FFB870 F09040 C06820 804010 301808"),
    presetPalette("Honey", PaletteCategory.WARM, "FFF8E0 FFE8B0 FFD080 F0B050 C08830 805818 302008"),
    presetPalette("Marigold", PaletteCategory.WARM, "FFF8D0 FFE8A0 FFD060 F0B030 C08018 805008 301808"),
    presetPalette("Tangerine", PaletteCategory.WARM, "FFE8D0 FFC8A0 FFA070 F07840 C05020 803010 301008"),
    presetPalette("Papaya", PaletteCategory.WARM, "FFE0C0 FFC090 FFA060 F07830 C05018 802808 300C08"),
    presetPalette("Flamingo", PaletteCategory.WARM, "FFD8D8 FFB0B0 FF8080 F05050 C02828 801010 300808"),
    presetPalette("Salmon", PaletteCategory.WARM, "FFE0D0 FFC0A8 FFA080 F07858 C05038 802818 300C08"),
    presetPalette("Melon", PaletteCategory.WARM, "FFE8D8 FFD0B0 FFB888 F09060 C06838 803818 301408"),
    presetPalette("Watermelon", PaletteCategory.WARM, "FFD8E0 FFB0C0 FF7890 F04860 C02038 800818 300408"),
    presetPalette("Strawberry", PaletteCategory.WARM, "FFD8D8 FFA8A8 FF7070 F03838 C01818 800808 300000"),
    presetPalette("Raspberry", PaletteCategory.WARM, "F8D0D8 E8A0B0 D07088 B84060 882038 501020 180408"),
    presetPalette("Cherry", PaletteCategory.WARM, "F8D0D0 E8A0A0 D07070 B84040 882020 501010 180000"),
    presetPalette("Poppy", PaletteCategory.WARM, "FFD8D0 FFB0A0 FF8870 F05840 C03020 801810 300808"),
    presetPalette("Brick", PaletteCategory.WARM, "E8D0C0 D0A890 B88060 986040 704028 482818 180C08"),
    presetPalette("Rust", PaletteCategory.WARM, "E8D0B8 D0A888 B88058 986038 704020 482810 180C08"),
    presetPalette("Terracotta", PaletteCategory.WARM, "F0D8C8 D8B098 C08868 A06040 784028 503018 201008"),
    presetPalette("Sienna", PaletteCategory.WARM, "E8D0B8 D0A880 B88050 986028 704010 482808 180C00"),
    presetPalette("Mahogany", PaletteCategory.WARM, "E0C8B8 C8A088 A87858 885838 603820 381810 140808")
)
