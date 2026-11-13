package dev.mnascimentos.aureole.core.data.model

/** Disk layout of the icon cache. Bumping [DIR_NAME] discards every icon cached by older versions. */
internal object IconCacheNaming {
    const val LEGACY_DIR_NAME = "app_icons"
    const val DIR_NAME = "app_icons_v2"
    private const val FULL_BLEED_SUFFIX = ".fb.png"
    private const val LEGACY_SUFFIX = ".png"

    fun fileName(packageName: String, fullBleed: Boolean): String =
        packageName + if (fullBleed) FULL_BLEED_SUFFIX else LEGACY_SUFFIX
}
