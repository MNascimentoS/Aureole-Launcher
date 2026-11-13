package dev.mnascimentos.aureole.composable.favorites.model

/**
 * [packages] with the packages of [shownOrder] rearranged into that order. The list shows only installed apps, so
 * each shown package takes one of the places the shown packages held and the others (apps not installed) keep
 * theirs. When [shownOrder] does not match the shown packages of [packages], nothing changes.
 */
fun reorderFavoritePackages(packages: List<String>, shownOrder: List<String>): List<String> {
    val shown = shownOrder.toSet()
    if (shown.size != shownOrder.size || packages.count { it in shown } != shownOrder.size) return packages
    val next = shownOrder.iterator()
    return packages.map { if (it in shown) next.next() else it }
}
