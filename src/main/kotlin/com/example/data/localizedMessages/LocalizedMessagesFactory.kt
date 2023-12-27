package com.example.data.localizedMessages

import com.example.data.localizedMessages.languages.ArabicLocalizedMessages
import com.example.data.localizedMessages.languages.EgyptianArabicLocalizedMessages
import com.example.data.localizedMessages.languages.EnglishLocalizedMessages
import org.koin.core.annotation.Single
import org.thechance.api_gateway.data.localizedMessages.languages.LocalizedMessages

@Single
class LocalizedMessagesFactory {
    fun createLocalizedMessages(languageCode: String): LocalizedMessages {
        return map[languageCode.uppercase()] ?: EnglishLocalizedMessages()
    }
}

private val map = mapOf(
    Language.ENGLISH.code to EnglishLocalizedMessages(),
    Language.ARABIC.code to ArabicLocalizedMessages(),
    Language.EGYPT.code to EgyptianArabicLocalizedMessages(),
)


enum class Language(val code: String) {
    ENGLISH("EN"),
    ARABIC("AR"),
    EGYPT("EG"),
    PALESTINE("PS"),
    SYRIA("SY"),
    IRAQ("IQ"),
}