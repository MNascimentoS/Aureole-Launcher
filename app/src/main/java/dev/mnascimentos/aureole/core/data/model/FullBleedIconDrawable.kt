package dev.mnascimentos.aureole.core.data.model

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable

/**
 * Bitmap icon that was read back from the disk cache and is already full bleed (an adaptive icon
 * without the system mask), so the UI may clip it with the corner radius chosen by the user.
 */
class FullBleedIconDrawable(resources: Resources, bitmap: Bitmap) : BitmapDrawable(resources, bitmap)
