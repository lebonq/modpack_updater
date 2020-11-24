package fr.lebonq.minecraft.assets;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import fr.lebonq.AppController;
import fr.lebonq.minecraft.RemoteMojangConfig;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.GetSha1;

public class Asset {
    private String aName;
    private String aHash;
    private int aSize;

    public Asset(String Name, String hash, int pSize) {
        this.aName = Name;
        this.aHash = hash;
        this.aSize = pSize;
    }

    /**
     * Permet de cree le dossier de lasset et de telechager le fichier Verifie aussi
     * si le fichier n'existe pas deja
     * 
     * @param pFile
     * @param pController
     */
    public void createFolderDownload(String pPath, AppController pController) {
        String vFolder = this.aHash.substring(0, 2);
        File vFile = new File(pPath + "/" + vFolder);
        File vCheckFile = new File(pPath + "/" + vFolder + "/" + this.aHash);
        if (vCheckFile.exists()) {// On check si le fichie existe
            try {
                if (GetSha1.calcSHA1(vCheckFile).toLowerCase().equals(this.aHash)) {// On met en lower case car le sha1
                                                                                    // retoune est en upper
                    System.out.println("SHA1 verifie! Pour " + this.aName);
                    pController.setUpdateLabel("Verification des fichiers");
                    return;// On peut return le SHA1 est bien verifier donc le telechargement a reussi
                }
                else{
                    vCheckFile.delete();//sinon on delete pour redl
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
        vFile.mkdirs();
        Downloader.downloadFile(RemoteMojangConfig.mcRessources.getaLink()+ vFolder +"/" + this.aHash, this.aName, false, pPath + "/" + vFolder +"/", this.aSize,false, pController);
    }
}
