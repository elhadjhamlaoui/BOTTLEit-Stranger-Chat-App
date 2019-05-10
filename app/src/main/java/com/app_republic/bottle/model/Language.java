package com.app_republic.bottle.model;

public class Language implements Comparable<Language>{
    public String code;
    public String name;

    public Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int compareTo(Language language) {
        return -1 * language.name.compareTo(this.name);
    }
}
