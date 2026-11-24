package dev.mnascimentos.aureole.screenshots.model

// Regras da guarda do contrato (`agents/aureole_screenshots.md` §4). Cada função devolve a lista de violações
// em texto; lista vazia = regra cumprida. `paths` são caminhos relativos à pasta `referencias`.

/** Prefixos legados aceitos durante a migração: vazio desde a fase 5 (a fase G apaga). */
val LEGADO_PERMITIDO: List<String> = emptyList()

const val MAX_PORTRAIT_STATES = 3

private val ID_REGEX = Regex("[a-z0-9]+")
private val ITEM_REGEX = Regex("[A-Z][A-Za-z0-9]*")
private val STEP_REGEX = Regex("P(\\d{2})[A-Z][A-Za-z0-9]*")
private const val FIRST_STATE = "Padrao"
private const val REF_SEGMENTS = 3

/** Regra 5: o manifesto abre e `contrato` é 1. */
fun manifestViolations(manifest: ScreenshotManifest?): List<String> = when {
    manifest == null -> listOf("screenshots.json ausente ou inválido")
    manifest.contrato != CONTRACT_VERSION -> listOf("contrato ${manifest.contrato} != $CONTRACT_VERSION")
    else -> emptyList()
}

/** Regra 1: PNG fora de `telas/`, `fluxos/`, `catalogo/` (ou fora da árvore do contrato) e fora do legado. */
fun outsideContractViolations(paths: List<String>, legacyAllowed: List<String>): List<String> = paths
    .filter { path -> parseScreenshotKey(path) == null && legacyAllowed.none { path.startsWith(it) } }
    .map { path -> "PNG fora do contrato: $path" }

/** Regra 2a: pasta ou item de referência que o manifesto não lista. */
fun unlistedReferenceViolations(manifest: ScreenshotManifest, keys: List<ScreenshotKey>): List<String> {
    val declared = declaredItems(manifest)
    return keys
        .filter { key -> itemKey(key.tipo, key.id, key.item) !in declared }
        .map { "PNG sem item no manifesto: $it" }
}

/** Regra 2b: estado, passo (sem `ref`) ou grupo do manifesto sem PNG. */
fun missingReferenceViolations(manifest: ScreenshotManifest, keys: List<ScreenshotKey>): List<String> {
    val present = keys.map { itemKey(it.tipo, it.id, it.item) }.toSet()
    return declaredItems(manifest)
        .filter { it !in present }
        .map { "item do manifesto sem PNG: $it" }
}

/** Regra 3: mais de 3 estados em retrato sem `excecao`; PNG de paisagem sem `paisagem: true`. */
fun ceilingViolations(manifest: ScreenshotManifest, keys: List<ScreenshotKey>): List<String> {
    val overCeiling = manifest.telas.orEmpty()
        .filter { it.excecao.isNullOrBlank() }
        .mapNotNull { tela ->
            val count = keys
                .filter { it.tipo == TIPO_TELAS && it.id == tela.id && !it.isLandscape }
                .map { it.item }
                .distinct()
                .size
            "tela ${tela.id} com $count estados em retrato".takeIf { count > MAX_PORTRAIT_STATES }
        }
    val landscapeAllowed = landscapeFolders(manifest)
    val landscape = keys
        .filter { it.isLandscape && it.folder !in landscapeAllowed }
        .map { "paisagem sem paisagem: true: $it" }
    return overCeiling + landscape
}

/** Regra 4a: ids de tela/fluxo/grupo em `[a-z0-9]+`; estados em PascalCase, o primeiro `Padrao`. */
fun idViolations(manifest: ScreenshotManifest): List<String> {
    val ids = manifest.telas.orEmpty().map { it.id } +
        manifest.fluxos.orEmpty().map { it.id } +
        manifest.catalogo.orEmpty().map { it.id }
    val badIds = ids.filter { it == null || !ID_REGEX.matches(it) }.map { "id inválido: $it" }
    val badStates = manifest.telas.orEmpty().flatMap { tela -> stateViolations(tela) }
    return badIds + badStates
}

/** Regra 4b: passos `P01`, `P02`… em sequência contínua. */
fun stepSequenceViolations(manifest: ScreenshotManifest): List<String> = manifest.fluxos.orEmpty()
    .flatMap { fluxo ->
        fluxo.passos.orEmpty().mapIndexedNotNull { index, passo ->
            val number = passo.id?.let { STEP_REGEX.matchEntire(it) }?.groupValues?.get(1)?.toInt()
            "fluxo ${fluxo.id}: passo ${passo.id} fora de sequência".takeIf { number != index + 1 }
        }
    }

/** Regra 4c: `ref` aponta para `telas/<id>/<estado>` que existe no manifesto. */
fun refViolations(manifest: ScreenshotManifest): List<String> {
    val states = manifest.telas.orEmpty()
        .flatMap { tela -> tela.estados.orEmpty().map { "$TIPO_TELAS/${tela.id}/${it.id}" } }
        .toSet()
    return manifest.fluxos.orEmpty()
        .flatMap { fluxo -> fluxo.passos.orEmpty().mapNotNull { it.ref } }
        .filter { ref -> ref.split('/').size != REF_SEGMENTS || ref !in states }
        .map { "ref para estado inexistente: $it" }
}

/** Todas as regras sobre o manifesto e os caminhos das referências. */
fun contractViolations(
    manifest: ScreenshotManifest?,
    paths: List<String>,
    legacyAllowed: List<String> = LEGADO_PERMITIDO
): List<String> {
    if (manifest == null || manifestViolations(manifest).isNotEmpty()) return manifestViolations(manifest)
    val keys = paths.mapNotNull { parseScreenshotKey(it) }
    return outsideContractViolations(paths, legacyAllowed) +
        unlistedReferenceViolations(manifest, keys) +
        missingReferenceViolations(manifest, keys) +
        ceilingViolations(manifest, keys) +
        idViolations(manifest) +
        stepSequenceViolations(manifest) +
        refViolations(manifest)
}

private fun stateViolations(tela: ScreenshotTela): List<String> {
    val estados = tela.estados.orEmpty()
    val badNames = estados
        .filter { it.id == null || !ITEM_REGEX.matches(it.id) }
        .map { "tela ${tela.id}: estado inválido ${it.id}" }
    val badFirst = listOf("tela ${tela.id}: primeiro estado não é $FIRST_STATE")
        .takeIf { estados.firstOrNull()?.id != FIRST_STATE }
        .orEmpty()
    return badNames + badFirst
}

/** Itens que precisam de PNG: `tipo/id/item`. No catálogo o item é o id do grupo em PascalCase. */
private fun declaredItems(manifest: ScreenshotManifest): Set<String> {
    val telas = manifest.telas.orEmpty().flatMap { tela ->
        tela.estados.orEmpty().map { itemKey(TIPO_TELAS, tela.id, it.id) }
    }
    val fluxos = manifest.fluxos.orEmpty().flatMap { fluxo ->
        fluxo.passos.orEmpty().filter { it.ref == null }.map { itemKey(TIPO_FLUXOS, fluxo.id, it.id) }
    }
    val grupos = manifest.catalogo.orEmpty().map { itemKey(TIPO_CATALOGO, it.id, it.id) }
    return (telas + fluxos + grupos).toSet()
}

private fun landscapeFolders(manifest: ScreenshotManifest): Set<String> {
    val telas = manifest.telas.orEmpty().filter { it.paisagem == true }.map { "$TIPO_TELAS/${it.id}" }
    val fluxos = manifest.fluxos.orEmpty().filter { it.paisagem == true }.map { "$TIPO_FLUXOS/${it.id}" }
    val grupos = manifest.catalogo.orEmpty().filter { it.paisagem == true }.map { "$TIPO_CATALOGO/${it.id}" }
    return (telas + fluxos + grupos).toSet()
}

/** No catálogo, o nome da função é o grupo em PascalCase: compara sem caixa. */
private fun itemKey(tipo: String, id: String?, item: String?): String {
    val normalized = if (tipo == TIPO_CATALOGO) item?.lowercase() else item
    return "$tipo/$id/$normalized"
}
