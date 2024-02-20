package com.example.boattask1.dks.language

interface DksLanguageListener {
    /**
     * Sends an update on the supported languages
     * @param defaultLanguage String The current default language
     * @param supportedLanguages ArrayList<String> The supported languages list
     */
    fun onDksSupportedLanguages(defaultLanguage: String?, supportedLanguages: ArrayList<String>?)
}