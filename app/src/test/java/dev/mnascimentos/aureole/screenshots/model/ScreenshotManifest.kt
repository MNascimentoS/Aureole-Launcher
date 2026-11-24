package dev.mnascimentos.aureole.screenshots.model

import com.google.gson.Gson

/**
 * Modelo do manifesto `app/src/screenshotTest/screenshots.json` (contrato v1, `agents/aureole_screenshots.md` §3).
 * Campos anuláveis porque o Gson não aplica valor padrão de construtor; leia com `orEmpty()`.
 */
data class ScreenshotManifest(
    val contrato: Int? = null,
    val referencias: String? = null,
    val telas: List<ScreenshotTela>? = null,
    val fluxos: List<ScreenshotFluxo>? = null,
    val catalogo: List<ScreenshotGrupo>? = null
)

data class ScreenshotTitulo(val pt: String? = null, val en: String? = null)

data class ScreenshotTela(
    val id: String? = null,
    val area: String? = null,
    val titulo: ScreenshotTitulo? = null,
    val paisagem: Boolean? = null,
    val excecao: String? = null,
    val idsAntigos: List<String>? = null,
    val estados: List<ScreenshotEstado>? = null
)

data class ScreenshotEstado(
    val id: String? = null,
    val titulo: ScreenshotTitulo? = null,
    val antes: List<String>? = null
)

data class ScreenshotFluxo(
    val id: String? = null,
    val area: String? = null,
    val titulo: ScreenshotTitulo? = null,
    val paisagem: Boolean? = null,
    val excecao: String? = null,
    val idsAntigos: List<String>? = null,
    val passos: List<ScreenshotPasso>? = null
)

data class ScreenshotPasso(
    val id: String? = null,
    val titulo: ScreenshotTitulo? = null,
    val ref: String? = null,
    val antes: List<String>? = null
)

data class ScreenshotGrupo(
    val id: String? = null,
    val titulo: ScreenshotTitulo? = null,
    val paisagem: Boolean? = null,
    val excecao: String? = null,
    val idsAntigos: List<String>? = null,
    val antes: List<String>? = null
)

/** Chave estável `<tipo>/<id>/<estado|passo|grupo>/<preview>` de uma imagem de referência. */
data class ScreenshotKey(val tipo: String, val id: String, val item: String, val preview: String) {
    val isLandscape: Boolean get() = preview.endsWith(LANDSCAPE_SUFFIX)
    val folder: String get() = "$tipo/$id"
    override fun toString(): String = "$tipo/$id/$item/$preview"
}

const val CONTRACT_VERSION = 1
const val TIPO_TELAS = "telas"
const val TIPO_FLUXOS = "fluxos"
const val TIPO_CATALOGO = "catalogo"
val CONTRACT_TYPES = listOf(TIPO_TELAS, TIPO_FLUXOS, TIPO_CATALOGO)

private const val LANDSCAPE_SUFFIX = "Landscape"
private const val FUNCTION_SUFFIX = "ScreenshotTest"
private const val CLASS_SUFFIX = "Kt"
private const val PNG_EXTENSION = ".png"
private const val CONTRACT_PATH_SEGMENTS = 4

/** Lê o manifesto; `null` quando o texto falta ou não é JSON válido. */
fun parseScreenshotManifest(json: String?): ScreenshotManifest? {
    if (json.isNullOrBlank()) return null
    return runCatching { Gson().fromJson(json, ScreenshotManifest::class.java) }.getOrNull()
}

/** `a/b/Classe/Funcao_preview_hash_0.png` → `a/b/Classe/Funcao_preview` (chave legada, sem hash e índice). */
fun legacyKey(relativePath: String): String {
    val withoutExtension = relativePath.removeSuffix(PNG_EXTENSION)
    val withoutIndex = withoutExtension.substringBeforeLast('_')
    return withoutIndex.substringBeforeLast('_')
}

/**
 * `<tipo>/<id>/<Classe>Kt/<Função>_<preview>_<hash>_<n>.png` → [ScreenshotKey].
 * `null` quando o caminho não segue a árvore do contrato.
 */
fun parseScreenshotKey(relativePath: String): ScreenshotKey? {
    val segments = relativePath.split('/')
    val valid = segments.size == CONTRACT_PATH_SEGMENTS &&
        segments[0] in CONTRACT_TYPES &&
        segments[2].endsWith(CLASS_SUFFIX) &&
        relativePath.endsWith(PNG_EXTENSION)
    if (!valid) return null
    val stem = legacyKey(segments[3])
    val function = stem.substringBeforeLast('_')
    val preview = stem.substringAfterLast('_')
    val item = function.removeSuffix(FUNCTION_SUFFIX)
    return if (!stem.contains('_') || item.isEmpty() || preview.isEmpty()) {
        null
    } else {
        ScreenshotKey(segments[0], segments[1], item, preview)
    }
}
