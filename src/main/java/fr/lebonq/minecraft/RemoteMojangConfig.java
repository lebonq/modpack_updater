package fr.lebonq.minecraft;

public enum RemoteMojangConfig{
    mcMetaData("https://launchermeta.mojang.com/"),
    mcLibraries("https://libraries.minecraft.net/"),
    mcRessources("https://resources.download.minecraft.net/"),
    mcVersionsList("https://launchermeta.mojang.com/mc/game/version_manifest.json");

    private String aLink;

    private RemoteMojangConfig(String pString){
        this.aLink = pString;
    }

    public String getaLink() {
        return aLink;
    }
}