package com.example

import com.example.data.model.SupportedLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SupportedLanguageTest {

    @Test
    fun testDefaultLanguageIsAuto() {
        val defaultLang = SupportedLanguage.fromCode(null)
        assertEquals(SupportedLanguage.AUTO, defaultLang)
    }

    @Test
    fun testLanguageLookupByCode() {
        assertEquals(SupportedLanguage.ASSAMESE, SupportedLanguage.fromCode("as"))
        assertEquals(SupportedLanguage.ENGLISH, SupportedLanguage.fromCode("en"))
        assertEquals(SupportedLanguage.BENGALI, SupportedLanguage.fromCode("bn"))
        assertEquals(SupportedLanguage.HINDI, SupportedLanguage.fromCode("hi"))
        assertEquals(SupportedLanguage.BODO, SupportedLanguage.fromCode("brx"))
    }

    @Test
    fun testAllLanguagesHaveInstructions() {
        SupportedLanguage.entries.forEach { lang ->
            assertNotNull(lang.promptInstruction)
            assert(lang.promptInstruction.isNotBlank())
            assert(lang.nativeName.isNotBlank())
            assert(lang.flagEmoji.isNotBlank())
        }
    }
}
