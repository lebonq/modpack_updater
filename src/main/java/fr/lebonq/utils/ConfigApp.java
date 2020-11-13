package fr.lebonq.utils;

import org.aeonbits.owner.Config;

@Config.Sources(value = "file:config/configServer.properties")
public interface ConfigApp extends Config {
    String modpackClient();
    String modsJson();
    String name();//Le nom qui apparaitera sur l'application
}