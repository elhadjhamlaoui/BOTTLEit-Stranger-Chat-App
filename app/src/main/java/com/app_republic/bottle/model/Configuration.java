package com.app_republic.bottle.model;



public class Configuration {
    private String label;
    private String  value;

    public Configuration(String label, String value){
        this.label = label;
        this.value = value;
    }

    public String getLabel(){
        return this.label;
    }

    public String getValue(){
        return this.value;
    }
}
