package dev.mnascimentos.aureole.feature.settings.model

/** The only place with the site host and the contact e-mails. */
object AppLinks {
    const val SITE_URL = "https://aureolelauncher.app"
    const val CONTACT_EMAIL = "contato@aureolelauncher.app"
    const val SUPPORT_EMAIL = "support@aureolelauncher.app"

    fun mailto(address: String): String = "mailto:$address"
}
