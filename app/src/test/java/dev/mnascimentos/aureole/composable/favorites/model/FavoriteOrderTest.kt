package dev.mnascimentos.aureole.composable.favorites.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FavoriteOrderTest {

    @Test
    fun `shown packages take the new order`() {
        assertEquals(listOf("c", "a", "b"), reorderFavoritePackages(listOf("a", "b", "c"), listOf("c", "a", "b")))
    }

    @Test
    fun `packages that are not shown keep their place`() {
        val packages = listOf("a", "gone", "b", "c")
        assertEquals(listOf("b", "gone", "c", "a"), reorderFavoritePackages(packages, listOf("b", "c", "a")))
    }

    @Test
    fun `a shown order that does not match changes nothing`() {
        val packages = listOf("a", "b")
        assertEquals(packages, reorderFavoritePackages(packages, listOf("a")))
        assertEquals(packages, reorderFavoritePackages(packages, listOf("a", "a")))
        assertEquals(packages, reorderFavoritePackages(packages, listOf("a", "x")))
        assertEquals(emptyList<String>(), reorderFavoritePackages(emptyList(), emptyList()))
    }
}
