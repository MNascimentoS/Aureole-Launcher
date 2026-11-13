package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

private const val APACHE_URL = "https://www.apache.org/licenses/LICENSE-2.0"
private const val OFL_URL = "https://openfontlicense.org"
private const val PLAY_CORE_TERMS_URL = "https://developer.android.com/guide/playcore/license"
private const val AUTHOR_GOOGLE = "Google LLC"
private const val AUTHOR_SQUARE = "Square, Inc."
private const val AUTHOR_ASTIGMATIC = "Astigmatic (Brian J. Bonislawsky)"
private const val GOOGLE_FONTS_OFL = "https://github.com/google/fonts/tree/main/ofl"

/** License of a listed item. [text] is the full text in `res/raw`; null when the license is only a link. */
enum class LicenseType(@RawRes val text: Int?, val url: String, @StringRes val label: Int) {
    APACHE_2_0(R.raw.license_apache_2_0, APACHE_URL, R.string.settings_licenses_apache),
    OFL_1_1(R.raw.license_ofl_1_1, OFL_URL, R.string.settings_licenses_ofl),
    PLAY_CORE_TERMS(null, PLAY_CORE_TERMS_URL, R.string.settings_licenses_terms)
}

/** Groups of the licenses screen, in the order they are shown. */
enum class OssSection(@StringRes val title: Int) {
    LIBRARIES(R.string.settings_licenses_section_libraries),
    ICONS(R.string.settings_licenses_section_icons),
    FONTS(R.string.settings_licenses_section_fonts)
}

/**
 * One item shipped in the APK. Names and authors are proper names, so they are not translated.
 *
 * @property fontResource For fonts, the `res/font` name prefix this entry covers (e.g. `istok_web`).
 */
data class OssEntry(
    val name: String,
    val author: String,
    val section: OssSection,
    val license: LicenseType,
    val projectUrl: String,
    val fontResource: String? = null
)

private fun library(name: String, author: String, url: String, license: LicenseType = LicenseType.APACHE_2_0) =
    OssEntry(name, author, OssSection.LIBRARIES, license, url)

private fun font(
    name: String,
    author: String,
    url: String,
    resource: String,
    license: LicenseType = LicenseType.OFL_1_1
) = OssEntry(name, author, OssSection.FONTS, license, url, resource)

val OpenSourceEntries: List<OssEntry> = listOf(
    library(
        "AndroidX and Jetpack Compose",
        "The Android Open Source Project",
        "https://developer.android.com/jetpack/androidx"
    ),
    library("Kotlin and kotlinx.coroutines", "JetBrains", "https://github.com/Kotlin/kotlinx.coroutines"),
    library("Koin", "Kotzilla", "https://github.com/InsertKoinIO/koin"),
    library("Coil", "Coil Contributors", "https://github.com/coil-kt/coil"),
    library("OkHttp and Okio", AUTHOR_SQUARE, "https://github.com/square/okhttp"),
    library("Haze", "Chris Banes", "https://github.com/chrisbanes/haze"),
    library("Gson", AUTHOR_GOOGLE, "https://github.com/google/gson"),
    library("Timber", "Jake Wharton", "https://github.com/JakeWharton/timber"),
    library("Firebase Crashlytics", AUTHOR_GOOGLE, "https://firebase.google.com/docs/crashlytics"),
    library(
        "Google Play In-App Updates",
        AUTHOR_GOOGLE,
        "https://developer.android.com/guide/playcore/in-app-updates",
        LicenseType.PLAY_CORE_TERMS
    ),
    OssEntry(
        "Material Symbols (Rounded)",
        AUTHOR_GOOGLE,
        OssSection.ICONS,
        LicenseType.APACHE_2_0,
        "https://github.com/google/material-design-icons"
    ),
    font("Andika", "SIL International", "https://github.com/silnrsi/font-andika", "andika"),
    font("Audiowide", AUTHOR_ASTIGMATIC, "$GOOGLE_FONTS_OFL/audiowide", "audiowide"),
    font("Chelsea Market", "Font Diner, Inc.", "$GOOGLE_FONTS_OFL/chelseamarket", "chelsea_market"),
    font("Istok Web", "Andrey V. Panov", "https://github.com/googlefonts/istok", "istok_web"),
    font("Montserrat", "The Montserrat Project Authors", "https://github.com/JulietaUla/Montserrat", "montserrat"),
    font("Orbitron", "The Orbitron Project Authors", "https://github.com/theleagueof/orbitron", "orbitron"),
    font("Roboto", "The Roboto Project Authors", "$GOOGLE_FONTS_OFL/roboto", "roboto_regular"),
    font("Roboto Mono", AUTHOR_GOOGLE, "https://github.com/googlefonts/RobotoMono", "roboto_mono"),
    font("Rubik", "The Rubik Project Authors", "https://github.com/googlefonts/rubik", "rubik"),
    font(
        "Space Grotesk",
        "The Space Grotesk Project Authors",
        "https://github.com/floriankarsten/space-grotesk",
        "space_grotesk"
    ),
    font("Space Mono", "The Space Mono Project Authors", "https://github.com/googlefonts/spacemono", "space_mono"),
    font(
        "Special Elite",
        AUTHOR_ASTIGMATIC,
        "https://github.com/google/fonts/tree/main/apache/specialelite",
        "special_elite",
        LicenseType.APACHE_2_0
    )
)

/** The entries split by section, in [OssSection] order, skipping empty sections. */
fun groupBySection(entries: List<OssEntry>): List<Pair<OssSection, List<OssEntry>>> =
    OssSection.entries.mapNotNull { section ->
        entries.filter { it.section == section }.takeIf { it.isNotEmpty() }?.let { section to it }
    }
