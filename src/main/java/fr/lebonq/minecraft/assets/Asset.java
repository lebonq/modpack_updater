package fr.lebonq.minecraft.assets;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;
import fr.lebonq.minecraft.RemoteMojangConfig;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.GetSha1;

public class Asset {
    private String aName;
    private String aHash;
    private int aSize;

    public Asset(String pName, String pHash, int pSize) {
        this.aName = pName;
        this.aHash = pHash;
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
                if (GetSha1.calcSHA1(vCheckFile).equalsIgnoreCase(this.aHash)) {
                    AppController.LOGGER.log(Level.INFO,"SHA1 verifie! Pour {}" , this.aName);
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
        Downloader.downloadFile(RemoteMojangConfig.MCRESSOURCES.getaLink()+ vFolder +"/" + this.aHash, this.aName, false, pPath + "/" + vFolder +"/", this.aSize,false, pController);
    }
}
