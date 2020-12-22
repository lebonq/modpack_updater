package fr.lebonq.minecraft;

public enum RemoteMojangConfig{
    MCMETADATA("https://launchermeta.mojang.com/"),
    MCLIBRARIES("https://libraries.minecraft.net/"),
    MCRESSOURCES("https://resources.download.minecraft.net/"),
    MCVERSIONSLIST("https://launchermeta.mojang.com/mc/game/version_manifest.json");

    private String aLink;

    private RemoteMojangConfig(String pString){
        this.aLink = pString;
    }

    public String getaLink() {
        return aLink;
    }
}