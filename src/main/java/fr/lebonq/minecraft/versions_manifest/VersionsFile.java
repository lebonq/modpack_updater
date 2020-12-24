package fr.lebonq.minecraft.versions_manifest;

import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;

/**
 * Nous permet d'enregistrer le fichier JSON qui liste les version
 */
public class VersionsFile {
    private Latest latest;
    private Version[] versions;

    public VersionsFile() {
        //Vide car utiliser par GSON
    }

    public Latest getLatest() {
        return this.latest;
    }

    public void setLatest(Latest latest) {
        this.latest = latest;
    }

    public Version[] getVersions() {
        return this.versions;
    }

    public void setVersions(Version[] versions) {
        this.versions = versions;
    }

    public String retriveUrlFromVersion(String pVersion){
        for(int i = 0; i < this.versions.length;i++){
            if(versions[i].getId().equals(pVersion)){
                return versions[i].getUrl();
            }
        }
        AppController.LOGGER.log(Level.FATAL,"Version introuvable");
        return null;
    }
}
