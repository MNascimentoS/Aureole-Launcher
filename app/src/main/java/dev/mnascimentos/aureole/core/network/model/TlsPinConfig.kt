package dev.mnascimentos.aureole.core.network.model

data class TlsPinConfig(
    val domain: String,
    val pins: List<String>,
    val expirationTimestampMillis: Long
)
