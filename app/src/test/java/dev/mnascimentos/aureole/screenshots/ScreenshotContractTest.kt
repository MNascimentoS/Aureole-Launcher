package dev.mnascimentos.aureole.screenshots

import dev.mnascimentos.aureole.screenshots.model.KdocTitle
import dev.mnascimentos.aureole.screenshots.model.ScreenshotKey
import dev.mnascimentos.aureole.screenshots.model.ScreenshotRef
import dev.mnascimentos.aureole.screenshots.model.ScreenshotSource
import dev.mnascimentos.aureole.screenshots.model.ceilingViolations
import dev.mnascimentos.aureole.screenshots.model.contractViolations
import dev.mnascimentos.aureole.screenshots.model.idViolations
import dev.mnascimentos.aureole.screenshots.model.kdocTitle
import dev.mnascimentos.aureole.screenshots.model.legacyKey
import dev.mnascimentos.aureole.screenshots.model.outsideContractViolations
import dev.mnascimentos.aureole.screenshots.model.parseScreenshotKey
import dev.mnascimentos.aureole.screenshots.model.parseScreenshotSource
import dev.mnascimentos.aureole.screenshots.model.refViolations
import dev.mnascimentos.aureole.screenshots.model.stepSequenceViolations
import dev.mnascimentos.aureole.screenshots.model.titleViolations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Guarda do contrato de screenshots v2 (`agents/aureole_screenshots.md` §4). JVM puro, diretório `app/`. */
class ScreenshotContractTest {

    private val homeSource = """
        /**
         * Home.
         *
         * @en Home
         * @area home
         * @paisagem
         */
        package dev.mnascimentos.aureole.telas.home

        /**
         * Padrão.
         *
         * A home com a grade cheia.
         *
         * @en Default
         */
        @PreviewTest
        @AureolePreview
        @AureoleLandscapePreview
        @Composable
        private fun PadraoScreenshotTest() = HomeCase()

        /** O caso montado; não é estado. */
        @Composable
        private fun HomeCase() = Unit

        /**
         * Canhoto.
         *
         * @en Left-handed
         */
        @PreviewTest
        @AureolePreview
        @Composable
        private fun CanhotoScreenshotTest() = HomeCase()
    """.trimIndent()
    private val criarPastaSource = """
        /**
         * Criar pasta.
         *
         * @en Create folder
         * @area home
         * @ref P02Formulario telas/home/Canhoto Formulário / Form
         */
        package dev.mnascimentos.aureole.fluxos.criarpasta

        /**
         * Segurar um app.
         *
         * @en Long-press an app
         */
        @PreviewTest
        @AureolePreview
        @Composable
        private fun P01SegurarAppScreenshotTest() = Unit
    """.trimIndent()
    private val sources = listOf(
        parseScreenshotSource("telas", "home", homeSource),
        parseScreenshotSource("fluxos", "criarpasta", criarPastaSource),
        ScreenshotSource(
            "catalogo",
            "controles",
            KdocTitle("Controles", "Controls"),
            emptyMap(),
            mapOf("Controles" to KdocTitle("Controles", "Controls"))
        )
    )
    private val paths = listOf(
        "telas/home/HomeTelaScreenshotTestKt/PadraoScreenshotTest_Dark Mode_c7acffac_0.png",
        "telas/home/HomeTelaScreenshotTestKt/PadraoScreenshotTest_Dark Mode Landscape_c7acffac_0.png",
        "telas/home/HomeTelaScreenshotTestKt/CanhotoScreenshotTest_Dark Mode_c7acffac_0.png",
        "fluxos/criarpasta/CriarpastaFluxoScreenshotTestKt/P01SegurarAppScreenshotTest_Dark Mode_c7acffac_0.png",
        "catalogo/controles/ControlesCatalogoScreenshotTestKt/ControlesScreenshotTest_Dark Mode_c7acffac_0.png"
    )
    private val keys = paths.mapNotNull { parseScreenshotKey(it) }

    @Test
    fun `repo real cumpre o contrato`() {
        val referenceDir = File("src/screenshotTestDebug/reference/dev/mnascimentos/aureole")
        val sourceDir = File("src/screenshotTest/java/dev/mnascimentos/aureole")
        val repoPaths = referenceDir.walkTopDown()
            .filter { it.isFile && it.extension == "png" }
            .map { it.relativeTo(referenceDir).invariantSeparatorsPath }
            .toList()
        val repoSources = listOf("telas", "fluxos", "catalogo").flatMap { tipo ->
            File(sourceDir, tipo).listFiles().orEmpty().filter { it.isDirectory }.flatMap { folder ->
                folder.listFiles().orEmpty().filter { it.extension == "kt" }
                    .map { parseScreenshotSource(tipo, folder.name, it.readText()) }
            }
        }

        assertEquals(emptyList<String>(), contractViolations(repoSources, repoPaths))
    }

    @Test
    fun `exemplo valido nao tem violacao`() {
        assertEquals(emptyList<String>(), contractViolations(sources, paths))
    }

    @Test
    fun `titulo do kdoc e a primeira frase e o en, sem descricao longa`() {
        val home = sources.first()

        assertEquals(KdocTitle("Home", "Home"), home.title)
        assertEquals(mapOf("en" to "Home", "area" to "home", "paisagem" to ""), home.tags)
        assertEquals(KdocTitle("Padrão", "Default"), home.items["Padrao"])
        assertEquals(KdocTitle("Canhoto", "Left-handed"), home.items["Canhoto"])
        assertEquals(KdocTitle("Fim sem recorde", null), kdocTitle(" * Fim sem recorde.\n * Depois explica."))
        assertNull(kdocTitle(" * O cartão encolhido no chip do canto superior direito, com a home livre para tocar."))
    }

    @Test
    fun `ref no kdoc do fluxo vira passo sem png`() {
        val fluxo = sources[1]

        assertEquals(
            listOf(
                ScreenshotRef(
                    "P02Formulario",
                    "telas/home/Canhoto",
                    KdocTitle("Formulário", "Form"),
                    "P02Formulario telas/home/Canhoto Formulário / Form"
                )
            ),
            fluxo.refs
        )
        assertEquals(mapOf("en" to "Create folder", "area" to "home"), fluxo.tags)
    }

    @Test
    fun `chave estavel corta classe, sufixo, hash e indice`() {
        val key = parseScreenshotKey(
            "telas/home/HomeTelaScreenshotTestKt/PadraoScreenshotTest_Dark Mode Landscape_c7acffac_0.png"
        )

        assertEquals(ScreenshotKey("telas", "home", "Padrao", "Dark Mode Landscape"), key)
        assertEquals("telas/home/Padrao/Dark Mode Landscape", key.toString())
        assertTrue(key!!.isLandscape)
        assertNull(parseScreenshotKey("feature/home/HomeScreenScreenshotTestKt/X_Dark Mode_c7acffac_0.png"))
        assertNull(parseScreenshotKey("telas/home/Padrao_Dark Mode_c7acffac_0.png"))
        assertNull(parseScreenshotKey("telas/home/HomeTelaScreenshotTestKt/Padrao_c7acffac_0.png"))
    }

    @Test
    fun `chave legada so perde hash e indice`() {
        val folder = "feature/home/HomeScreenScreenshotTestKt"

        assertEquals(
            "$folder/Home_shouldRenderScreenshotTest_Dark Mode",
            legacyKey("$folder/Home_shouldRenderScreenshotTest_Dark Mode_c7acffac_0.png")
        )
    }

    @Test
    fun `regra 1 png fora de telas, fluxos e catalogo`() {
        val legacy = "composable/AppIconRadiusScreenshotTestKt/AppIconRadius24ScreenshotTest_Dark Mode_c7acffac_0.png"
        val loose = "outros/home/XKt/Padrao_Dark Mode_c7acffac_0.png"

        assertEquals(2, outsideContractViolations(paths + legacy + loose).size)
    }

    @Test
    fun `regra 2 pasta e funcao com png precisam de titulo no kdoc`() {
        val semArquivo = parseScreenshotKey(
            "telas/busca/BuscaTelaScreenshotTestKt/PadraoScreenshotTest_Dark Mode_c_0.png"
        )!!
        val semDoc = parseScreenshotKey(
            "telas/home/HomeTelaScreenshotTestKt/VazioScreenshotTest_Dark Mode_c_0.png"
        )!!
        val semIngles = sources.first().copy(title = KdocTitle("Home", null))
        val soRefSemTitulo = sources[1].copy(id = "atalho", title = null)

        assertEquals(
            listOf(
                "pasta sem arquivo de teste: telas/busca",
                "telas/home/Vazio sem título no KDoc da função (primeira frase e @en)"
            ),
            titleViolations(sources, keys + semArquivo + semDoc).sorted()
        )
        assertEquals(
            listOf("telas/home sem título no KDoc do arquivo (primeira frase e @en)"),
            titleViolations(listOf(semIngles), keys.filter { it.tipo == "telas" })
        )
        assertEquals(
            listOf("fluxos/atalho sem título no KDoc do arquivo (primeira frase e @en)"),
            titleViolations(listOf(soRefSemTitulo), emptyList())
        )
    }

    @Test
    fun `regra 3 teto de estados e paisagem`() {
        val estados = listOf("Padrao", "Vazio", "Erro", "Longo")
        val buscaKeys = estados.map { ScreenshotKey("telas", "busca", it, "Dark Mode") } +
            ScreenshotKey("telas", "busca", "Padrao", "Dark Mode Landscape")
        val busca =
            ScreenshotSource("telas", "busca", KdocTitle("Busca", "Search"), mapOf("area" to "home"), emptyMap())
        val liberada = busca.copy(tags = busca.tags + mapOf("excecao" to "x D-01", "paisagem" to ""))

        assertEquals(2, ceilingViolations(listOf(busca), buscaKeys).size)
        assertTrue(ceilingViolations(listOf(liberada), buscaKeys).isEmpty())
    }

    @Test
    fun `regra 4 ids, padrao, area, sequencia e ref`() {
        val ruins = listOf(
            ScreenshotKey("telas", "todos-apps", "Padrao", "Dark Mode"),
            ScreenshotKey("telas", "vazia", "vazio", "Dark Mode"),
            ScreenshotKey("fluxos", "fluxo", "P01Abrir", "Dark Mode"),
            ScreenshotKey("fluxos", "fluxo", "P03Fechar", "Dark Mode")
        )
        val semArea = ScreenshotSource("telas", "vazia", KdocTitle("Vazia", "Empty"), emptyMap(), emptyMap())
        val fluxo = ScreenshotSource(
            "fluxos",
            "fluxo",
            KdocTitle("Fluxo", "Flow"),
            mapOf("area" to "home"),
            emptyMap(),
            listOf(
                ScreenshotRef("P04Ver", "telas/home/Inexistente", KdocTitle("Ver", "See"), "P04Ver …"),
                ScreenshotRef(null, null, null, "P05 sem titulo")
            )
        )

        // todos-apps; vazia: estado minúsculo, sem Padrao e sem @area.
        assertEquals(4, idViolations(listOf(semArea), ruins).size)
        assertEquals(1, stepSequenceViolations(listOf(fluxo), ruins).size)
        assertEquals(
            listOf(
                "fluxos/fluxo: ref para estado inexistente: telas/home/Inexistente",
                "fluxos/fluxo: @ref fora do formato: P05 sem titulo"
            ),
            refViolations(listOf(fluxo), keys)
        )
        assertTrue(idViolations(sources, keys).isEmpty())
        assertTrue(stepSequenceViolations(sources, keys).isEmpty())
        assertTrue(refViolations(sources, keys).isEmpty())
    }
}
