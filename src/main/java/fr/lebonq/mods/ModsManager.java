package fr.lebonq.mods;

import java.io.File;

import fr.lebonq.ServerConfig;
import fr.lebonq.files.FilesManager;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.IsContain;

public class ModsManager {
    private Mod[] aModsList;
    private FilesManager aFilesManager;
    private int aNumberOfMods;
    private double aTotalSize;
    private ServerConfig aServerConfig;

    /**
     * Constructeur dans lequel on cree aussi notre list de mods
     * @param pListJars List de .jar des mods
     */
    public ModsManager(File[] pListJars, FilesManager pManger, ServerConfig pServerConfig){
        this.aServerConfig = pServerConfig;
        this.aNumberOfMods = pListJars.length;
        this.aModsList = new Mod[this.aNumberOfMods];
        this.aFilesManager = pManger;

        for(int i =0; i < pListJars.length;i++){
            File vJson = this.aFilesManager.extractJson(pListJars[i]);//On recupre le fichier JSON
            String[] vInfo = ModJsonManager.getModInfo(vJson); // On recupere les info dans le ficgier JSON
            this.aModsList[i] = new Mod(pListJars[i], vInfo[0],vInfo[1]);
            vJson.deleteOnExit();
            this.aTotalSize += this.aModsList[i].getSize();
        }
    }

    /**
     * Permet d'ecrire une liste detaillee des mods
     */
    public void printModList(){
        for(int i = 0; i < this.aNumberOfMods;i++){
            System.out.println(this.aModsList[i]);
        }
        System.out.println("Les mods prennent un espace totale de " + (int)this.aTotalSize + " Mo");
    }

    public int getNumberOfMods(){
        return this.aNumberOfMods;
    }

    public void updateModFiles(final String[][] pFiles){
        int vNbFiles = pFiles.length-1;
        int i = 0; 
        int j = 0;
        while(i < vNbFiles){
            if(!(this.aModsList[i].getName().equals(pFiles[j][0]))){//Si les noms sont differente c'est que le mods distant manque en local
                if(IsContain.isContain(i, 0, pFiles, this.aModsList[i].getName())){
                    Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[j][2].replaceAll(".json", ""), pFiles[j][0], false, "mods/", pFiles[j][2].replaceAll(".json", ""));
                    //Ici les replaceAll permet de retirer le .json du path du fichier
                    j++;//on avance j mais pas i
                }
                else{
                    System.out.println("Suppression d'un mod obselete");
                    this.aModsList[i].getFile().delete();
                    i++;//On avance i car le mods doit etre supprimer
                }
            }
            else{
                if(!(this.aModsList[i].getVersion().equals(pFiles[j][1]))){//On compare les versions si differente on met a jour
                    this.aModsList[i].getFile().delete();
                    Downloader.downloadFile(this.aServerConfig.modpackClient() + pFiles[j][2].replaceAll(".json", ""), pFiles[j][2], false, "mods/", pFiles[j][2].replaceAll(".json", ""));
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
