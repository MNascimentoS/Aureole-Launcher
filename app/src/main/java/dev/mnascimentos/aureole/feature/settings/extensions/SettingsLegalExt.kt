package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import dev.mnascimentos.aureole.feature.settings.model.LegalPage
import dev.mnascimentos.aureole.feature.settings.model.legalPageUrl
import dev.mnascimentos.aureole.util.IntentUtils
import java.util.Locale

/** Opens the privacy policy or the terms page, in the language of the device. */
fun openLegalPage(context: Context, page: LegalPage) {
    IntentUtils.openUrl(context, legalPageUrl(page, Locale.getDefault()))
}
