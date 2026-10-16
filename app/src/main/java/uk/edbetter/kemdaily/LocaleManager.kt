package uk.edbetter.kemdaily

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * App language options. "system" follows the device language; every other entry maps to a
 * values-xx resource folder that ships translated strings.
 */
enum class AppLanguage(val tag: String, val displayName: String) {
    SYSTEM("system", "System default"),
    ENGLISH("en", "English"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    GERMAN("de", "Deutsch"),
    PORTUGUESE("pt", "Português"),
    HINDI("hi", "हिन्दी"),
    CHINESE("zh", "中文"),
    JAPANESE("ja", "日本語"),
    RUSSIAN("ru", "Русский"),
    INDONESIAN("id", "Bahasa Indonesia"),
    TURKISH("tr", "Türkçe"),
    PERSIAN("fa", "فارسی"),
    YORUBA("yo", "Yorùbá");

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: SYSTEM
    }
}

/** Wraps a base [Context] with the user's selected locale so string resources resolve correctly. */
object LocaleManager {
    fun wrap(context: Context, languageTag: String?): Context {
        val language = AppLanguage.fromTag(languageTag)
        if (language == AppLanguage.SYSTEM) return context

        val locale = Locale.forLanguageTag(language.tag)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}

