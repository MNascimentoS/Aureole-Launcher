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

        init {
            require(args.size <= MAX_ARGS) { "TextResource supports at most $MAX_ARGS format args" }
        }
    }

    data class Raw(val text: String) : TextResource

    companion object {
        const val MAX_ARGS = 3

        fun fromString(text: String): TextResource = Raw(text)
        fun fromResId(@StringRes resId: Int, vararg args: Any): TextResource = Resource(resId, *args)
    }
}

@Composable
@ReadOnlyComposable
fun TextResource.asString(): String = when (this) {
    is TextResource.Resource -> formatResource()
    is TextResource.Raw -> text
}

@Composable
@ReadOnlyComposable
private fun TextResource.Resource.formatResource(): String = when (args.size) {
    0 -> stringResource(resId)
    1 -> stringResource(resId, args[0])
    2 -> stringResource(resId, args[0], args[1])
    else -> stringResource(resId, args[0], args[1], args[2])
}
