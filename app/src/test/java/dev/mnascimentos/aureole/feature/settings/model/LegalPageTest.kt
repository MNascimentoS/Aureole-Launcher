package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class LegalPageTest {

    @Test
    fun `privacy in portuguese returns pt-br url`() {
        assertEquals(
            "https://aureole.mnascimentos.dev/privacy/pt-br/",
            legalPageUrl(LegalPage.PRIVACY, Locale("pt", "BR"))
        )
    }

    @Test
    fun `privacy in english returns en url`() {
        assertEquals(
            "https://aureole.mnascimentos.dev/privacy/en/",
            legalPageUrl(LegalPage.PRIVACY, Locale.ENGLISH)
        )
    }

    @Test
    fun `terms in portuguese returns pt-br url`() {
        assertEquals(
            "https://aureole.mnascimentos.dev/terms/pt-br/",
            legalPageUrl(LegalPage.TERMS, Locale("pt", "PT"))
        )
    }

    @Test
    fun `terms in english returns en url`() {
        assertEquals(
            "https://aureole.mnascimentos.dev/terms/en/",
            legalPageUrl(LegalPage.TERMS, Locale.US)
        )
    }

    @Test
    fun `any other language falls back to en`() {
        assertEquals(
            "https://aureole.mnascimentos.dev/privacy/en/",
            legalPageUrl(LegalPage.PRIVACY, Locale.JAPANESE)
        )
        assertEquals(
            "https://aureole.mnascimentos.dev/terms/en/",
            legalPageUrl(LegalPage.TERMS, Locale.FRANCE)
        )
    }

    @Test
    fun `contributors list has aislan then mateus with github urls`() {
        assertEquals(
            listOf("https://github.com/thisaislan", "https://github.com/MNascimentoS"),
            Contributors.map { it.githubUrl }
        )
    }
}
