package dev.mnascimentos.aureole.screenshots.model

// Regras da guarda do contrato v2 (`agents/aureole_screenshots.md` §4). Cada função devolve a lista de violações
// em texto; lista vazia = regra cumprida. `paths` são caminhos relativos à pasta das referências; `sources` são os
// arquivos de teste de `telas/`, `fluxos/` e `catalogo/` já lidos ([parseScreenshotSource]). Não há manifesto.

const val MAX_PORTRAIT_STATES = 3

private val ID_REGEX = Regex("[a-z0-9]+")
private val ITEM_REGEX = Regex("[A-Z][A-Za-z0-9]*")
private val STEP_REGEX = Regex("P(\\d{2})[A-Z][A-Za-z0-9]*")
private val AREAS = setOf("home", "ajustes", "jogo")
private const val FIRST_STATE = "Padrao"
private const val REF_SEGMENTS = 3
private const val TAG_AREA = "area"
private const val TAG_LANDSCAPE = "paisagem"
private const val TAG_EXCEPTION = "excecao"
private const val TITLE_HINT = "primeira frase e @en"

/** Regra 1: PNG fora de `telas/`, `fluxos/`, `catalogo/` (ou fora da árvore do contrato). */
fun outsideContractViolations(paths: List<String>): List<String> = paths
    .filter { path -> parseScreenshotKey(path) == null }
    .map { path -> "PNG fora do contrato: $path" }

/**
 * Regra 2: toda pasta com PNG tem um arquivo de teste com título (pt e `@en`) no KDoc antes do `package`, e toda
 * função com PNG tem título no KDoc dela.
 */
fun titleViolations(sources: List<ScreenshotSource>, keys: List<ScreenshotKey>): List<String> {
    val byFolder = sources.associateBy { "${it.tipo}/${it.id}" }
    val folders = keys.map { it.folder }.toSet()
    val refOnly = sources
        .filter { "${it.tipo}/${it.id}" !in folders && it.refs.isNotEmpty() && it.title?.isComplete != true }
        .map { "${it.tipo}/${it.id} sem título no KDoc do arquivo ($TITLE_HINT)" }
    return refOnly + keys.groupBy { it.folder }.flatMap { (folder, inFolder) ->
        val source = byFolder[folder]
        when {
            source == null -> listOf("pasta sem arquivo de teste: $folder")
            else -> listOfNotNull(
                "$folder sem título no KDoc do arquivo ($TITLE_HINT)".takeIf { source.title?.isComplete != true }
            ) + inFolder.map { it.item }.distinct().mapNotNull { item ->
                val title = source.items.entries.firstOrNull { sameItem(source.tipo, it.key, item) }?.value
                "$folder/$item sem título no KDoc da função ($TITLE_HINT)".takeIf { title?.isComplete != true }
            }
        }
    }
}

/** Regra 3: mais de 3 estados em retrato sem `@excecao`; PNG de paisagem sem `@paisagem`. */
fun ceilingViolations(sources: List<ScreenshotSource>, keys: List<ScreenshotKey>): List<String> {
    val byFolder = sources.associateBy { "${it.tipo}/${it.id}" }
    val overCeiling = keys.filter { it.tipo == TIPO_TELAS && !it.isLandscape }
        .groupBy { it.folder }
        .filter { (folder, list) ->
            list.map { it.item }.distinct().size > MAX_PORTRAIT_STATES &&
                byFolder[folder]?.tags?.get(TAG_EXCEPTION).isNullOrBlank()
        }
        .map { (folder, list) -> "$folder com ${list.map { it.item }.distinct().size} estados em retrato" }
    val landscape = keys
        .filter { it.isLandscape && byFolder[it.folder]?.tags?.containsKey(TAG_LANDSCAPE) != true }
        .map { "paisagem sem @paisagem: $it" }
    return overCeiling + landscape
}

/**
 * Regra 4a: ids de pasta em `[a-z0-9]+`; estados em PascalCase e toda tela com `Padrao`; tela e fluxo com
 * `@area` (`home`, `ajustes` ou `jogo`).
 */
fun idViolations(sources: List<ScreenshotSource>, keys: List<ScreenshotKey>): List<String> {
    val folders = (keys.map { it.tipo to it.id } + sources.map { it.tipo to it.id }).distinct()
    val badIds = folders.filter { !ID_REGEX.matches(it.second) }.map { "id inválido: ${it.first}/${it.second}" }
    val telas = keys.filter { it.tipo == TIPO_TELAS }.groupBy { it.id }
    val badStates = telas.flatMap { (id, list) ->
        list.map { it.item }.distinct().filter { !ITEM_REGEX.matches(it) }.map { "tela $id: estado inválido $it" } +
            listOfNotNull("tela $id: sem o estado $FIRST_STATE".takeIf { list.none { it.item == FIRST_STATE } })
    }
    val badAreas = sources.filter { it.tipo != TIPO_CATALOGO && it.tags[TAG_AREA] !in AREAS }
        .map { "${it.tipo}/${it.id} sem @area home|ajustes|jogo" }
    return badIds + badStates + badAreas
}

/** Regra 4b: passos `P01`, `P02`… em sequência contínua, contando os com PNG e os com `ref`. */
fun stepSequenceViolations(sources: List<ScreenshotSource>, keys: List<ScreenshotKey>): List<String> {
    val withPng = keys.filter { it.tipo == TIPO_FLUXOS }.map { it.id to it.item }
    val refs = sources.filter { it.tipo == TIPO_FLUXOS }
        .flatMap { fluxo -> fluxo.refs.mapNotNull { ref -> ref.step?.let { fluxo.id to it } } }
    val byFlow = (withPng + refs).distinct().groupBy({ it.first }, { it.second })
    return byFlow.flatMap { (fluxo, steps) ->
        val numbers = steps.map { STEP_REGEX.matchEntire(it)?.groupValues?.get(1)?.toInt() }
        val bad = steps.filterIndexed { i, _ -> numbers[i] == null }
            .map { "fluxo $fluxo: passo $it fora do padrão P<NN>" }
        val sorted = numbers.filterNotNull().sorted()
        bad + listOfNotNull(
            "fluxo $fluxo: passos fora de sequência $sorted".takeIf { sorted != (1..sorted.size).toList() }
        )
    }
}

/**
 * Regra 4c: `@ref <passo> telas/<id>/<estado> <pt> / <en>` bem formado, só em fluxo, apontando para um estado de tela
 * que tem PNG.
 */
fun refViolations(sources: List<ScreenshotSource>, keys: List<ScreenshotKey>): List<String> {
    val states = keys.filter { it.tipo == TIPO_TELAS }.map { "$TIPO_TELAS/${it.id}/${it.item}" }.toSet()
    return sources.flatMap { source ->
        source.refs.mapNotNull { ref ->
            val where = "${source.tipo}/${source.id}"
            when {
                source.tipo != TIPO_FLUXOS -> "$where: @ref só em fluxo"
                ref.step == null || ref.title?.isComplete != true -> "$where: @ref fora do formato: ${ref.line}"
                ref.target?.split('/')?.size != REF_SEGMENTS || ref.target !in states ->
                    "$where: ref para estado inexistente: ${ref.target}"
                else -> null
            }
        }
    }
}

/** Todas as regras sobre os testes e os caminhos das referências. */
fun contractViolations(sources: List<ScreenshotSource>, paths: List<String>): List<String> {
    val keys = paths.mapNotNull { parseScreenshotKey(it) }
    return outsideContractViolations(paths) +
        titleViolations(sources, keys) +
        ceilingViolations(sources, keys) +
        idViolations(sources, keys) +
        stepSequenceViolations(sources, keys) +
        refViolations(sources, keys)
}

/** No catálogo, o nome da função é o grupo em PascalCase: compara sem caixa. */
private fun sameItem(tipo: String, function: String, item: String): Boolean =
    if (tipo == TIPO_CATALOGO) function.equals(item, ignoreCase = true) else function == item
