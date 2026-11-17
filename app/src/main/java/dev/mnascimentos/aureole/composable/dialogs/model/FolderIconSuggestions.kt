package dev.mnascimentos.aureole.composable.dialogs.model

import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import java.text.Normalizer

/** How many icons the folder form suggests for a name. */
const val FOLDER_ICON_SUGGESTION_COUNT = 5

/** Suggested when the name says nothing the dictionary knows. */
private val defaultSuggestions = listOf("folder", "star", "favorite", "work", "home")

private val money = listOf("account_balance", "payments", "credit_card", "savings", "lock")
private val work = listOf("work", "business_center", "description", "calendar_month", "mail")
private val games = listOf("sports_esports", "extension", "casino", "trophy", "rocket_launch")
private val social = listOf("group", "chat", "forum", "groups", "public")
private val music = listOf("music_note", "headphones", "podcasts", "mic", "play_circle")
private val video = listOf("movie", "play_circle", "photo_library", "podcasts", "music_note")
private val photo = listOf("photo_camera", "photo_library", "palette", "share", "favorite")
private val shopping = listOf("shopping_cart", "shopping_bag", "receipt_long", "credit_card", "payments")
private val health = listOf("fitness_center", "health_and_safety", "monitor_heart", "self_improvement", "sports_soccer")
private val food = listOf("restaurant", "local_cafe", "shopping_bag", "directions_car", "location_on")
private val travel = listOf("flight", "luggage", "hotel", "beach_access", "map")
private val transport = listOf("directions_car", "map", "location_on", "train", "flight")
private val tools = listOf("build", "settings", "calculate", "lightbulb", "code")
private val security = listOf("lock", "security", "health_and_safety", "cloud", "badge")
private val study = listOf("school", "menu_book", "lightbulb", "translate", "edit_note")
private val news = listOf("newspaper", "public", "notifications", "mail", "podcasts")
private val house = listOf("home", "lightbulb", "wifi", "pets", "local_cafe")
private val code = listOf("code", "terminal", "build", "cloud", "smartphone")
private val ai = listOf("star", "bolt", "lightbulb", "chat", "code")
private val favorites = listOf("favorite", "star", "thumb_up", "bolt", "home")
private val files = listOf("cloud", "folder", "description", "download", "assignment")
private val productivity = listOf("assignment", "calendar_month", "edit_note", "work", "lightbulb")
private val communication = listOf("call", "mail", "chat", "forum", "notifications")
private val investing = listOf("trending_up", "savings", "account_balance", "payments", "receipt_long")
private val system = listOf("settings", "build", "smartphone", "wifi", "security")

/**
 * Local dictionary (Portuguese and English, no network) from a word of the folder name to the icons that fit it,
 * best first. Words are lower case and without accents; a trailing "s" is also tried ("bancos" -> "banco").
 */
private val dictionary: Map<String, List<String>> = buildMap {
    fun put(icons: List<String>, vararg words: String) = words.forEach { put(it, icons) }
    put(money, "banco", "bank", "banking", "financas", "finance", "money", "dinheiro", "pagamento", "carteira")
    put(money, "wallet", "conta", "account")
    put(work, "trabalho", "work", "office", "escritorio", "business", "negocio", "empresa", "job")
    put(games, "jogo", "game", "gaming", "games")
    put(social, "social", "rede", "amigo", "friend", "mensagem", "message", "chat")
    put(music, "musica", "music", "som", "audio", "radio")
    put(video, "video", "filme", "movie", "tv", "streaming", "midia", "media", "series")
    put(photo, "foto", "photo", "camera", "imagem", "image", "galeria", "gallery")
    put(shopping, "compra", "shopping", "loja", "store", "mercado", "market", "shop")
    put(health, "saude", "health", "fitness", "academia", "gym", "esporte", "sport", "treino", "workout")
    put(food, "comida", "food", "restaurante", "restaurant", "delivery", "lanche")
    put(travel, "viagem", "viagens", "travel", "trip", "turismo", "ferias", "vacation")
    put(transport, "transporte", "transport", "mapa", "map", "carro", "car", "mobilidade", "uber", "taxi")
    put(tools, "ferramenta", "tool", "utilitario", "utility", "utilities", "util", "utils")
    put(system, "sistema", "system", "config", "configuracoes", "settings", "ajustes")
    put(security, "seguranca", "security", "senha", "password", "privacidade", "privacy")
    put(study, "estudo", "escola", "school", "study", "educacao", "education", "livro", "book", "leitura", "reading")
    put(news, "noticia", "news", "jornal")
    put(house, "casa", "home", "lar", "house")
    put(code, "dev", "codigo", "code", "programacao", "programming")
    put(ai, "ia", "ias", "ai")
    put(favorites, "favorito", "favorite", "favorites")
    put(files, "nuvem", "cloud", "arquivo", "file", "documento", "document", "docs")
    put(productivity, "produtividade", "productivity", "tarefa", "task", "nota", "note")
    put(communication, "comunicacao", "communication", "telefone", "phone", "email", "mail", "contato", "contact")
    put(investing, "investimento", "investment", "crypto", "cripto", "bolsa", "stock")
}

/**
 * The [FOLDER_ICON_SUGGESTION_COUNT] icons suggested for a folder called [name] ("Suggested for ..."): those of
 * the words the dictionary knows, then a catalog icon named like a word, then general ones; no repeats. When
 * [current] (the icon the folder has, old keys included) is not among them it takes the first place, so the marked
 * icon is always in view; when it is, the row keeps its order (picking a suggestion does not move it).
 */
fun folderIconSuggestions(name: String, current: String? = null): List<String> {
    val words = normalizedWords(name)
    val fromDictionary = words.flatMap { word -> dictionary[word] ?: dictionary[word.removeSuffix("s")].orEmpty() }
    val fromCatalog = words.mapNotNull(AureoleIconCatalog::resolveKey)
    val base = (fromDictionary + fromCatalog + defaultSuggestions).distinct().take(FOLDER_ICON_SUGGESTION_COUNT)
    val currentKey = current?.let { AureoleIconCatalog.resolveKey(it) ?: it } ?: return base
    return if (currentKey in base) base else listOf(currentKey) + base.dropLast(1)
}

/** Lower case words of [text] without accents ("Finanças pessoais" -> [financas, pessoais]). */
internal fun normalizedWords(text: String): List<String> =
    Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace(DIACRITICS, "")
        .lowercase()
        .split(NON_WORD)
        .filter { it.isNotEmpty() }

private val DIACRITICS = Regex("\\p{Mn}+")
private val NON_WORD = Regex("[^\\p{L}\\p{N}]+")
