package de.claved.origin.utils.enums;

public enum Language {

    GERMAN, ENGLISH;

    public <T> T language(T german, T english) {
        return (equals(Language.GERMAN) ? german : english);
    }
}
