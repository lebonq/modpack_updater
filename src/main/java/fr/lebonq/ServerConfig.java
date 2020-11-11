package fr.lebonq;

import org.aeonbits.owner.Config;

@Config.Sources(value = "file:config/configServer.properties")
public interface ServerConfig extends Config {
    String modpackClient();
    String modsJson();
}