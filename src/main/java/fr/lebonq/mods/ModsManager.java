package fr.lebonq.mods;

import java.io.File;

import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;
import fr.lebonq.files.FilesManager;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.IsContain;
import fr.lebonq.utils.ConfigApp;

public class ModsManager {
    private Mod[] aModsList;
    private FilesManager aFilesManager;
    private int aNumberOfMods;
    private double aTotalSize;
    private ConfigApp aServerConfig;
    private AppController aController;

    /**
     * Constructeur dans lequel on cree aussi notre list de mods
     * 
     * @param pListJars List de .jar des mods
     * @param pManager Premt de gerer les fichier
     * @param pServerConfig Permet de recupere l'adresse serveur
     * @param pController Permet de communiquer avec l"ui
     */
    public ModsManager(File[] pListJars, FilesManager pManager, ConfigApp pServerConfig, AppController pController) {
        this.aServerConfig = pServerConfig;
        this.aNumberOfMods = pListJars.length;
        this.aModsList = new Mod[this.aNumberOfMods];
        this.aFilesManager = pManager;
        this.aController = pController;

        for (int i = 0; i < pListJars.length; i++) {
            File vJson = this.aFilesManager.extractFromJar(pListJars[i], "fabric.mod.json");// On recupre le fichier JSON
            String[] vInfo = ModJsonManager.getModInfo(vJson); // On recupere les info dans le fichier JSON
            this.aModsList[i] = new Mod(pListJars[i], vInfo[0], vInfo[1],this.aFilesManager.extractFromJar(pListJars[i], vInfo[2]), vInfo[3]);
            vJson.deleteOnExit();
            this.aTotalSize += this.aModsList[i].getSize();
        }
    }

    /**
     * Permet d'ecrire une liste detaillee des mods
     */
    public void printModList() {
        for (int i = 0; i < this.aNumberOfMods; i++) {
            AppController.LOGGER.log(Level.INFO,this.aModsList[i]);
        }
        this.aController.setLittleUpdateLabel("Les mods prennent un espace total de " + (int)this.aTotalSize + " Mo");
        AppController.LOGGER.log(Level.INFO,"Les mods prennent un espace total de {} Mo",(int)this.aTotalSize);
    }

    public int getNumberOfMods(){
        return this.aNumberOfMods;
    }

    public Mod[] getMods(){
        return this.aModsList;
    }
    
    public void updateModFiles(final String[][] pFiles){

        for (int remote = 0; remote < pFiles.length; remote++) {
            for (int local = 0; local < this.aNumberOfMods; local++) {
                if (pFiles[remote][0].equals(this.aModsList[local].getName())) {
                    if (!(pFiles[remote][1].equals(this.aModsList[local].getVersion()))) {
                        this.aModsList[local].getFile().delete();
                        Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[remote][2].replaceAll(".json", ""), pFiles[remote][0], false, this.aFilesManager.getModFolder().getAbsolutePath() + "/", 0, true, this.aController);
                    }
                    break;
                }
                if (local == this.aNumberOfMods-1){
                    Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[remote][2].replaceAll(".json", ""), pFiles[remote][0], false, this.aFilesManager.getModFolder().getAbsolutePath()+"/", 0,true,this.aController);
                }
            }
            if(this.aNumberOfMods == 0){
                Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[remote][2].replaceAll(".json", ""), pFiles[remote][0], false, this.aFilesManager.getModFolder().getAbsolutePath() + "/", 0, true, this.aController);
            }
        }

        for (int local = 0; local < this.aNumberOfMods; local++) {
            for (int remote = 0; remote < pFiles.length; remote++) {
                if(pFiles[remote][0].equals(this.aModsList[local].getName())){
                    break;
                }
                if (remote == pFiles.length - 1) {
                    this.aModsList[local].getFile().delete();
                }
            }
        }
    }
}
