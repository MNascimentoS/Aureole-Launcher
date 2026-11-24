package dev.mnascimentos.aureole.screenshots

import dev.mnascimentos.aureole.screenshots.model.LEGADO_PERMITIDO
import dev.mnascimentos.aureole.screenshots.model.ScreenshotEstado
import dev.mnascimentos.aureole.screenshots.model.ScreenshotFluxo
import dev.mnascimentos.aureole.screenshots.model.ScreenshotGrupo
import dev.mnascimentos.aureole.screenshots.model.ScreenshotKey
import dev.mnascimentos.aureole.screenshots.model.ScreenshotManifest
import dev.mnascimentos.aureole.screenshots.model.ScreenshotPasso
import dev.mnascimentos.aureole.screenshots.model.ScreenshotTela
import dev.mnascimentos.aureole.screenshots.model.ceilingViolations
import dev.mnascimentos.aureole.screenshots.model.contractViolations
import dev.mnascimentos.aureole.screenshots.model.idViolations
import dev.mnascimentos.aureole.screenshots.model.legacyKey
import dev.mnascimentos.aureole.screenshots.model.manifestViolations
import dev.mnascimentos.aureole.screenshots.model.missingReferenceViolations
import dev.mnascimentos.aureole.screenshots.model.outsideContractViolations
import dev.mnascimentos.aureole.screenshots.model.parseScreenshotKey
import dev.mnascimentos.aureole.screenshots.model.parseScreenshotManifest
import dev.mnascimentos.aureole.screenshots.model.refViolations
import dev.mnascimentos.aureole.screenshots.model.stepSequenceViolations
import dev.mnascimentos.aureole.screenshots.model.unlistedReferenceViolations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Guarda do contrato de screenshots (`agents/aureole_screenshots.md` §4). JVM puro, diretório de trabalho `app/`. */
class ScreenshotContractTest {

    private val home = ScreenshotTela(
        id = "home",
        paisagem = true,
        estados = listOf(ScreenshotEstado(id = "Padrao"), ScreenshotEstado(id = "Canhoto"))
    )
    private val criarPasta = ScreenshotFluxo(
        id = "criarpasta",
        passos = listOf(
            ScreenshotPasso(id = "P01SegurarApp"),
            ScreenshotPasso(id = "P02Formulario", ref = "telas/home/Canhoto")
        )
    )
    private val manifest = ScreenshotManifest(
        contrato = 1,
        telas = listOf(home),
        fluxos = listOf(criarPasta),
        catalogo = listOf(ScreenshotGrupo(id = "controles"))
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
        val manifestFile = File("src/screenshotTest/screenshots.json")
        val referenceDir = File("src/screenshotTestDebug/reference/dev/mnascimentos/aureole")
        val repoPaths = referenceDir.walkTopDown()
            .filter { it.isFile && it.extension == "png" }
            .map { it.relativeTo(referenceDir).invariantSeparatorsPath }
            .toList()

        val violations = contractViolations(parseScreenshotManifest(manifestFile.readText()), repoPaths)

        assertEquals(emptyList<String>(), violations)
    }

    @Test
    fun `exemplo valido nao tem violacao`() {
        assertEquals(emptyList<String>(), contractViolations(manifest, paths, legacyAllowed = emptyList()))
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
    fun `regra 5 manifesto ausente, invalido ou com outro contrato`() {
        assertNull(parseScreenshotManifest(null))
        assertNull(parseScreenshotManifest("{ nao e json"))
        assertNotNull(parseScreenshotManifest("""{"contrato": 1}"""))
        assertEquals(1, manifestViolations(null).size)
        assertEquals(1, manifestViolations(ScreenshotManifest(contrato = 2)).size)
        assertEquals(1, contractViolations(ScreenshotManifest(contrato = 2), paths).size)
        assertTrue(manifestViolations(manifest).isEmpty())
    }

    @Test
    fun `regra 1 png fora de telas, fluxos e catalogo`() {
        val legacy = "feature/home/HomeScreenScreenshotTestKt/HomeDefaultScreenshotTest_Dark Mode_c7acffac_0.png"
        val loose = "outros/home/XKt/Padrao_Dark Mode_c7acffac_0.png"

        assertEquals(2, outsideContractViolations(paths + legacy + loose, emptyList()).size)
        assertEquals(1, outsideContractViolations(paths + legacy + loose, LEGADO_PERMITIDO).size)
    }

    @Test
    fun `regra 2 pasta fora do manifesto e item sem png`() {
        val extra = parseScreenshotKey("telas/busca/BuscaTelaScreenshotTestKt/PadraoScreenshotTest_Dark Mode_c_0.png")!!
        val semPasso = keys.filterNot { it.tipo == "fluxos" }

        assertEquals(
            listOf("PNG sem item no manifesto: telas/busca/Padrao/Dark Mode"),
            unlistedReferenceViolations(manifest, keys + extra)
        )
        assertEquals(
            listOf("item do manifesto sem PNG: fluxos/criarpasta/P01SegurarApp"),
            missingReferenceViolations(manifest, semPasso)
        )
    }

    @Test
    fun `regra 3 teto de estados e paisagem`() {
        val estados = listOf("Padrao", "Vazio", "Erro", "Longo")
        val tela = ScreenshotTela(id = "busca", estados = estados.map { ScreenshotEstado(id = it) })
        val buscaKeys = estados.map { ScreenshotKey("telas", "busca", it, "Dark Mode") } +
            ScreenshotKey("telas", "busca", "Padrao", "Dark Mode Landscape")
        val comTeto = ScreenshotManifest(contrato = 1, telas = listOf(tela))
        val liberada = tela.copy(excecao = "x D-01", paisagem = true)
        val comExcecao = ScreenshotManifest(contrato = 1, telas = listOf(liberada))

        assertEquals(2, ceilingViolations(comTeto, buscaKeys).size)
        assertTrue(ceilingViolations(comExcecao, buscaKeys).isEmpty())
    }

    @Test
    fun `regra 4 ids, primeiro estado, sequencia e ref`() {
        val ruim = ScreenshotManifest(
            contrato = 1,
            telas = listOf(ScreenshotTela(id = "todos-apps", estados = listOf(ScreenshotEstado(id = "vazio")))),
            fluxos = listOf(
                ScreenshotFluxo(
                    id = "fluxo",
                    passos = listOf(
                        ScreenshotPasso(id = "P01Abrir"),
                        ScreenshotPasso(id = "P03Fechar"),
                        ScreenshotPasso(id = "P04Ver", ref = "telas/home/Inexistente")
                    )
                )
            )
        )

        assertEquals(3, idViolations(ruim).size)
        assertEquals(2, stepSequenceViolations(ruim).size)
        assertEquals(listOf("ref para estado inexistente: telas/home/Inexistente"), refViolations(ruim))
        assertTrue(idViolations(manifest).isEmpty())
        assertTrue(stepSequenceViolations(manifest).isEmpty())
        assertTrue(refViolations(manifest).isEmpty())
    }
}
