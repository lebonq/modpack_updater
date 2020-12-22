package fr.lebonq.minecraft.libraries;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.GetSha1;

public class Librarie {
    protected String aPath;
    protected String aSha1;
    protected File aFile;
    protected int aSize;
    protected String aUrl;
    protected boolean aNative;
    protected boolean aHasSHA1;
    protected LibrarieNative aNativeLibrarie;// Librarie native assoicer a celle ci

    public Librarie(String aPath, String aSha1, int aSize, String aUrl,boolean pHasSHA1) {
        this.aPath = aPath;
        this.aSha1 = aSha1;
        this.aSize = aSize;
        this.aUrl = aUrl;
        this.aNative = false;
        this.aHasSHA1 = pHasSHA1;
    }

    public String getPath() {
        return this.aPath.replace("/", "\\");
    }

    public String getSha1() {
        return this.aSha1;
    }

    public int getSize() {
        return this.aSize;
    }

    public String getUrl() {
        return this.aUrl;
    }

    public boolean hasNative() {
        return this.aNative;
    }

    public LibrarieNative getNativeLibrarie() {
        return this.aNativeLibrarie;
    }

    public void setNativeLibrarie(LibrarieNative aNativeLibrarie) {
        this.aNativeLibrarie = aNativeLibrarie;
        this.aNative = true;
    }

    public boolean equals(String aSha1) {
        return this.aSha1.equals(aSha1);
    }

    /**
     * Permet de telecharger le libraries et son natives ou de check le SHA1
     * 
     * @param pClientPath Le path vers le dossier libaries du client
     * @param pController
     */
    public void download(String pClientPath, AppController pController) {
        if (this.aNative) {// On dl la librairie natives associee
            this.aNativeLibrarie.download(pClientPath, pController);
        }
        File vFile = new File(pClientPath + "/" + this.aPath);
        if (vFile.exists() && this.aHasSHA1) {// On check si le fichie existe
            try {
                if (GetSha1.calcSHA1(vFile).equalsIgnoreCase(this.aSha1)) {// On met en lower case car le sha1 retoune est en upper
                    this.aFile = vFile;
                    AppController.LOGGER.log(Level.INFO,"SHA1 verifie! Pour {}" ,vFile.getName());
                    return;// On peut return le SHA1 est bien verifier donc le telechargement a reussi
                } else {
                    if(vFile.delete()){// Sinon on supprime le fichier
                        AppController.LOGGER.log(Level.INFO,"La suppression de {} a echoue", vFile.getName());
                    }
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
        String vPathWhitoutFileName = pClientPath + "/" + this.aPath.substring(0, this.aPath.lastIndexOf("/"));
        File vFileMkdirs = new File(vPathWhitoutFileName);
        vFileMkdirs.mkdirs();
        this.aFile = Downloader.downloadFile(this.aUrl, this.aUrl.substring(this.aUrl.lastIndexOf("/") + 1), false,
                vPathWhitoutFileName, this.aSize,false, pController);
    }

    public File getFile() {
        return this.aFile;
    }

    public void extractNatives(File pBinPath) {
        if (this.aNative){
            try {
                this.aNativeLibrarie.extractNative(pBinPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
