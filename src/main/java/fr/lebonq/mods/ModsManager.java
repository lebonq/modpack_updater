package fr.lebonq.mods;

import java.io.File;

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
            File vJson = this.aFilesManager.extractFromJar(pListJars[i], "fabric.mod.json");// On recupre le fichier
                                                                                            // JSON
            String[] vInfo = ModJsonManager.getModInfo(vJson); // On recupere les info dans le ficgier JSON
            this.aModsList[i] = new Mod(pListJars[i], vInfo[0], vInfo[1],
                    this.aFilesManager.extractFromJar(pListJars[i], vInfo[2]), vInfo[3]);
            vJson.deleteOnExit();
            this.aTotalSize += this.aModsList[i].getSize();
        }
    }

    /**
     * Permet d'ecrire une liste detaillee des mods
     */
    public void printModList() {
        for (int i = 0; i < this.aNumberOfMods; i++) {
            System.out.println(this.aModsList[i]);
        }
        this.aController.setLittleUpdateLabel("Les mods prennent un espace totale de " + (int)this.aTotalSize + " Mo");
        System.out.println("Les mods prennent un espace totale de " + (int)this.aTotalSize + " Mo");
    }

    public int getNumberOfMods(){
        return this.aNumberOfMods;
    }

    public Mod[] getMods(){
        return this.aModsList;
    }
    
    public void updateModFiles(final String[][] pFiles){
        int vNbFiles = pFiles.length-1;
        int i = 0; 
        int j = 0;
        while(i < vNbFiles){
            if(i == this.aNumberOfMods){//Si nous avons deja parcouru tout les mods locaux alors on telecharge tout les restes
                while(i <= vNbFiles){
                    Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[j][2].replaceAll(".json", ""), pFiles[j][0], false, "mods/", pFiles[j][2].replaceAll(".json", ""),this.aController);
                    this.aController.updateList();
                    i++;j++;
                }
                return;
            }
            if(!(this.aModsList[i].getName().equals(pFiles[j][0]))){//Si les noms sont differente c'est que le mods distant manque en local
                if(IsContain.isContain(i, 0, pFiles, this.aModsList[i].getName())){
                    Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[j][2].replaceAll(".json", ""), pFiles[j][0], false, "mods/", pFiles[j][2].replaceAll(".json", ""),this.aController);
                    this.aController.updateList();
                    //Ici les replaceAll permet de retirer le .json du path du fichier
                    j++;//on avance j mais pas i
                }
                else{
                    this.aController.setLittleUpdateLabel("Suppression d'un mod obselete");
                    System.out.println("Suppression d'un mod obselete");
                    this.aModsList[i].getFile().delete();
                    i++;//On avance i car le mods doit etre supprimer
                }
            }
            else{
                if(!(this.aModsList[i].getVersion().equals(pFiles[j][1]))){//On compare les versions si differente on met a jour
                    this.aModsList[i].getFile().delete();
                    Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[j][2].replaceAll(".json", ""), pFiles[j][0], false, "mods/", pFiles[j][2].replaceAll(".json", ""),this.aController);
                    this.aController.updateList();
                    i++;
                    j++;
                }
                else{
                    i++;
                    j++;
                }
            }
        }
    }
}
