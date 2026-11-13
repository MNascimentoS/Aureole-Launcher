package dev.mnascimentos.aureole.core.designsystem.theme

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Protege `res/font` contra arquivos que não são fontes (ex.: página 404 do GitHub salva como .ttf).
 * Teste em JVM puro: lê os arquivos direto do módulo, sem Robolectric.
 */
class FontResourceSignatureTest {

    private val fontDir = File("src/main/res/font")

    private fun fontFiles(): List<File> =
        fontDir.listFiles { file -> file.isFile }.orEmpty().sortedBy { it.name }

    private fun hasFontSignature(header: ByteArray): Boolean {
        if (header.size < SIGNATURE_SIZE) return false
        val truetype = header.copyOfRange(0, SIGNATURE_SIZE).contentEquals(byteArrayOf(0, 1, 0, 0))
        val text = String(header, 0, SIGNATURE_SIZE, Charsets.ISO_8859_1)
        return truetype || text == "OTTO" || text == "true"
    }

    @Test
    fun `font directory should be found and not empty`() {
        assertTrue("Pasta ${fontDir.absolutePath} nao encontrada", fontDir.isDirectory)
        assertTrue("Nenhuma fonte em ${fontDir.path}", fontFiles().isNotEmpty())
    }

    @Test
    fun `every font file should start with a valid font signature`() {
        val invalid = fontFiles().filterNot { file ->
            hasFontSignature(file.inputStream().use { it.readNBytes(SIGNATURE_SIZE) })
        }.map { it.name }

        assertTrue("Arquivos que nao sao fontes: $invalid", invalid.isEmpty())
    }

    @Test
    fun `every font file should be larger than 10 KB`() {
        val tooSmall = fontFiles().filter { it.length() <= MIN_FONT_BYTES }.map { it.name }

        assertTrue("Fontes pequenas demais: $tooSmall", tooSmall.isEmpty())
    }

    @Test
    fun `signature check should reject html pages`() {
        assertTrue(!hasFontSignature("<!DOCTYPE html>".toByteArray()))
        assertTrue(!hasFontSignature(ByteArray(2)))
    }

    @Test
    fun `signature check should accept truetype opentype and legacy mac headers`() {
        assertTrue(hasFontSignature(byteArrayOf(0, 1, 0, 0)))
        assertTrue(hasFontSignature("OTTO".toByteArray()))
        assertTrue(hasFontSignature("true".toByteArray()))
    }

    private companion object {
        const val SIGNATURE_SIZE = 4
        const val MIN_FONT_BYTES = 10 * 1024L
    }
}
