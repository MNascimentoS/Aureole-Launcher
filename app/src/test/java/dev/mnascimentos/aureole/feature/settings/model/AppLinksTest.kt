package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLinksTest {

    @Test
    fun `when building a mailto, should prefix the address`() {
        assertEquals("mailto:support@aureolelauncher.app", AppLinks.mailto(AppLinks.SUPPORT_EMAIL))
        assertEquals("mailto:contato@aureolelauncher.app", AppLinks.mailto(AppLinks.CONTACT_EMAIL))
    }

    @Test
    fun `when reading the site url, should use the new domain with no trailing slash`() {
        assertEquals("https://aureolelauncher.app", AppLinks.SITE_URL)
    }
}
