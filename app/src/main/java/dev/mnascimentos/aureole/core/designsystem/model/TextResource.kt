package dev.mnascimentos.aureole.core.designsystem.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource

sealed interface TextResource {

    data class Resource(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : TextResource {
        constructor(@StringRes resId: Int, vararg args: Any) : this(resId, args.toList())
    }

    data class Raw(val text: String) : TextResource

    companion object {
        fun fromString(text: String): TextResource = Raw(text)
        fun fromResId(@StringRes resId: Int, vararg args: Any): TextResource = Resource(resId, *args)
    }
}

@Composable
@ReadOnlyComposable
fun TextResource.asString(): String = when (this) {
    is TextResource.Resource -> stringResource(resId, *args.toTypedArray())
    is TextResource.Raw -> text
}
