package dev.mnascimentos.aureole.core.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchRankingTest {

    private fun rank(items: List<String>, query: String) = rankByQuery(items, query) { it }

    @Test
    fun `normalizeForSearch strips accents and lowercases`() {
        assertEquals("camera ao", normalizeForSearch("CÂMERA Ão"))
    }

    @Test
    fun `accents and case are ignored on both sides`() {
        assertEquals(listOf("Câmera"), rank(listOf("Câmera", "Galeria"), "CAMERA"))
        assertEquals(listOf("Camera"), rank(listOf("Camera", "Galeria"), "câm"))
    }

    @Test
    fun `labels that start with the query come before labels that only contain it`() {
        val result = rank(listOf("Mapa de Fotos", "Fotos", "Álbum de fotos", "Foto Pro"), "fot")
        assertEquals(listOf("Foto Pro", "Fotos", "Álbum de fotos", "Mapa de Fotos"), result)
    }

    @Test
    fun `ties are broken alphabetically ignoring accents`() {
        assertEquals(listOf("Água", "Aviso", "Azul"), rank(listOf("Azul", "Aviso", "Água"), "a"))
    }

    @Test
    fun `blank query returns the list as it came`() {
        val items = listOf("b", "a", "c")
        assertEquals(items, rank(items, ""))
        assertEquals(items, rank(items, "   "))
    }

    @Test
    fun `query is trimmed`() {
        assertEquals(listOf("Maps"), rank(listOf("Maps", "Music"), "  ma "))
    }

    @Test
    fun `no match returns empty list`() {
        assertTrue(rank(listOf("Maps", "Music"), "zzz").isEmpty())
    }

    @Test
    fun `label selector is used for arbitrary items`() {
        val result = rankByQuery(listOf(1 to "Beta", 2 to "Alpha"), "a") { it.second }
        assertEquals(listOf(2, 1), result.map { it.first })
    }
}
