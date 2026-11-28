package dev.mnascimentos.aureole.screenshots.model

/**
 * O que o KDoc de um arquivo de teste de screenshot diz (contrato v2, `agents/aureole_screenshots.md` §3): o título da
 * tela/fluxo/grupo e as tags dele (`@area`, `@paisagem`, `@excecao`) no KDoc antes do `package`, e o título de cada
 * estado/passo no KDoc da função `<Item>ScreenshotTest`; passo de fluxo que é um estado de tela vem como `@ref`
 * no KDoc do arquivo. É o mesmo texto que o TeuSpecs e o TeuApps mostram.
 */
data class ScreenshotSource(
    val tipo: String,
    val id: String,
    val title: KdocTitle?,
    val tags: Map<String, String>,
    /** Item (função sem `ScreenshotTest`) → título; `null` quando a função não tem KDoc com título. */
    val items: Map<String, KdocTitle?>,
    /** Passos sem PNG: `@ref P01Passo telas/<id>/<Estado> Título / Title`. */
    val refs: List<ScreenshotRef> = emptyList()
)

/** Um `@ref`; campos `null` quando a linha não segue `<passo> <tipo/id/estado> <pt> / <en>`. */
data class ScreenshotRef(val step: String?, val target: String?, val title: KdocTitle?, val line: String)

/** Primeira frase do KDoc ([pt]) e a do `@en` ([en]); cada uma com até [MAX_TITLE] caracteres. */
data class KdocTitle(val pt: String?, val en: String?) {
    val isComplete: Boolean get() = pt != null && en != null
}

/** Mais longo que isso é descrição, não título: os leitores mostram o nome da função no lugar. */
const val MAX_TITLE = 60

private const val TAG_REF = "ref"
private const val REF_STEP = 1
private const val REF_TARGET = 2
private const val REF_PT = 3
private const val REF_EN = 4

private val FILE_DOC = Regex("""^\s*/\*\*(.*?)\*/\s*package\s""", RegexOption.DOT_MATCHES_ALL)
private val FUNCTION = Regex("""fun\s+(\w+)ScreenshotTest\s*\(""")
private const val KDOC = """/\*\*((?:(?!\*/).)*)\*/\s*"""
private const val ANNOTATIONS = """(?:@[\w.]+(?:\([^)]*\))?\s*)*"""
private const val TEST_FUNCTION = """(?:(?:private|internal)\s+)?fun\s+(\w+)ScreenshotTest\s*\("""
private val FUNCTION_DOC = Regex(KDOC + ANNOTATIONS + TEST_FUNCTION, RegexOption.DOT_MATCHES_ALL)
private val REF_LINE = Regex("""^(\S+)\s+(\S+)\s+(.+?)\s+/\s+(.+)$""")
private val TAG_LINE = Regex("""^@([a-z]\w*)\s*(.*)$""")
private val SENTENCE = Regex("""^(.+?[.!?])(?:\s|$)""")
private val PARAGRAPH = Regex("""\n\s*\n""")
private val SPACES = Regex("""\s+""")

/**
 * O texto de um KDoc (sem `/**`, `*/` e os `*` do começo das linhas) separado em corpo e tags `@nome valor`, na
 * ordem (uma tag pode repetir, como `@ref`).
 */
private fun kdocParts(doc: String): Pair<String, List<Pair<String, String>>> {
    val lines = doc.lines().map { it.trim().removePrefix("*").trim() }
    val firstTag = lines.indexOfFirst { TAG_LINE.matches(it) }.let { if (it < 0) lines.size else it }
    val tags = lines.drop(firstTag).mapNotNull { TAG_LINE.matchEntire(it) }
        .map { it.groupValues[1] to it.groupValues[2].trim() }
    return lines.take(firstTag).joinToString("\n").trim() to tags
}

private fun parseRef(line: String): ScreenshotRef {
    val m = REF_LINE.matchEntire(line) ?: return ScreenshotRef(null, null, null, line)
    val g = m.groupValues
    return ScreenshotRef(
        g[REF_STEP],
        g[REF_TARGET],
        KdocTitle(firstSentence(g[REF_PT]), firstSentence(g[REF_EN])),
        line
    )
}

private fun firstSentence(text: String): String? {
    val paragraph = text.split(PARAGRAPH).first().replace(SPACES, " ").trim()
    val sentence = (SENTENCE.find(paragraph)?.groupValues?.get(1) ?: paragraph).removeSuffix(
        "."
    ).replace("`", "").trim()
    return sentence.takeIf { it.isNotEmpty() && it.length <= MAX_TITLE }
}

/** Título de um KDoc: a primeira frase e o `@en`. `null` quando não tem nenhum dos dois. */
fun kdocTitle(doc: String): KdocTitle? {
    val (body, tags) = kdocParts(doc)
    val en = tags.firstOrNull { it.first == "en" }?.second
    val title = KdocTitle(firstSentence(body), en?.let(::firstSentence))
    return title.takeIf { it.pt != null || it.en != null }
}

/** Lê um arquivo de teste da pasta `<tipo>/<id>/`. */
fun parseScreenshotSource(tipo: String, id: String, source: String): ScreenshotSource {
    val fileDoc = FILE_DOC.find(source)?.groupValues?.get(1)
    val documented = FUNCTION_DOC.findAll(source).associate { it.groupValues[2] to kdocTitle(it.groupValues[1]) }
    val items = FUNCTION.findAll(source).map { it.groupValues[1] }.associateWith { documented[it] }
    val tags = fileDoc?.let { kdocParts(it).second }.orEmpty()
    return ScreenshotSource(
        tipo = tipo,
        id = id,
        title = fileDoc?.let(::kdocTitle),
        tags = tags.filter { it.first != TAG_REF }.toMap(),
        items = items,
        refs = tags.filter { it.first == TAG_REF }.map { parseRef(it.second) }
    )
}
