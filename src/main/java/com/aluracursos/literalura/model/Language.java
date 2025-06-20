package com.aluracursos.literalura.model;

public enum Language {

    INGLES("en", "Inglés"),
    ESPANOL("es", "Español"),
    FRANCES("fr", "Francés"),
    UNKNOWN("unknown", "Desconocido"); // para otros idiomas

    private String languageAPI;
    private String ownLanguage;

    Language(String languageAPI, String ownLanguage) {
        this.languageAPI = languageAPI;
        this.ownLanguage = ownLanguage;
    }

    public String getLanguageAPI() {
        return languageAPI;
    }

    public void setLanguageAPI(String languageAPI) {
        this.languageAPI = languageAPI;
    }

    public String getOwnLanguage() {
        return ownLanguage;
    }

    public void setOwnLanguage(String ownLanguage) {
        this.ownLanguage = ownLanguage;
    }

    // String ==> "Language" (enum)
    public static Language fromString (String text) {
        try {
            for (Language language : Language.values()) {
                if (language.languageAPI.equalsIgnoreCase(text)) {
                    return language;
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("No pudo ser definido el idioma: (" + text + ")");
        }
        return UNKNOWN;
    }
}
