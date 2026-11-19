package dev.mnascimentos.aureole.composable.dialogs.model

import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FolderIconSuggestionsTest {

    @Test
    fun `bank names suggest money icons, in Portuguese and English`() {
        val expected = listOf("account_balance", "payments", "credit_card", "savings", "lock")

        assertEquals(expected, folderIconSuggestions("Banco"))
        assertEquals(expected, folderIconSuggestions("bank"))
        assertEquals(expected, folderIconSuggestions("Finanças"))
    }

    @Test
    fun `accents, case and a plural are ignored`() {
        assertEquals(folderIconSuggestions("saude"), folderIconSuggestions("SAÚDE"))
        assertEquals(folderIconSuggestions("jogo"), folderIconSuggestions("Jogos"))
        assertEquals("sports_esports", folderIconSuggestions("Games").first())
    }

    @Test
    fun `a word that names a catalog icon suggests it`() {
        assertTrue("pets" in folderIconSuggestions("My pets"))
    }

    @Test
    fun `an unknown name gets the general suggestions`() {
        assertEquals(listOf("folder", "star", "favorite", "work", "home"), folderIconSuggestions("Xyzzy"))
        assertEquals(folderIconSuggestions("Xyzzy"), folderIconSuggestions(""))
    }

    @Test
    fun `there are always five suggestions without repeats`() {
        listOf("", "Banco", "Trabalho e estudo", "Jogos", "a b c", "Música e vídeo").forEach { name ->
            val suggestions = folderIconSuggestions(name)

            assertEquals(name, FOLDER_ICON_SUGGESTION_COUNT, suggestions.size)
            assertEquals(name, suggestions.distinct(), suggestions)
        }
    }

    @Test
    fun `a current icon outside the suggestions comes first`() {
        val suggestions = folderIconSuggestions("Banco", current = "pets")

        assertEquals("pets", suggestions.first())
        assertEquals(FOLDER_ICON_SUGGESTION_COUNT, suggestions.size)
    }

    @Test
    fun `a current icon among the suggestions keeps the order`() {
        assertEquals(folderIconSuggestions("Banco"), folderIconSuggestions("Banco", current = "savings"))
    }

    @Test
    fun `an old current key is shown as the icon it resolves to`() {
        assertEquals("shopping_cart", folderIconSuggestions("Xyzzy", current = "cart").first())
    }

    @Test
    fun `every suggested key exists in the catalog`() {
        val names = listOf(
            "banco", "trabalho", "jogos", "social", "musica", "video", "fotos", "compras", "saude", "comida",
            "viagem", "transporte", "ferramentas", "sistema", "seguranca", "escola", "noticias", "casa", "codigo",
            "ia", "favoritos", "arquivos", "produtividade", "telefone", "investimentos", "Xyzzy"
        )
        names.flatMap { folderIconSuggestions(it) }.forEach { key ->
            assertEquals(key, key, AureoleIconCatalog.resolveKey(key))
        }
    }

    @Test
    fun `words are split on anything that is not a letter or digit`() {
        assertEquals(listOf("financas", "pessoais", "2"), normalizedWords("Finanças-pessoais 2!"))
    }
}
