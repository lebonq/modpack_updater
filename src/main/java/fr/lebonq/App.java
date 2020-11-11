package fr.lebonq;

import java.io.IOException;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;

import fr.lebonq.files.FilesManager;
import fr.lebonq.mods.ModsManager;
import fr.lebonq.remote.Downloader;

class App {

    public static void main(String[] args) {

        FilesManager vFilesManager = new FilesManager();
        ModsManager vModsManager = null;
        final Properties vProperties = new Properties();
        ServerConfig vServerConfig = ConfigFactory.create(ServerConfig.class);

        try {
            vProperties.load(vFilesManager.getClass().getClassLoader().getResourceAsStream("project.properties")); //Permet d'acceder au fichier ressoruces pour recupérer la version du fichier pom.xml
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("Modpack_updater version " + vProperties.getProperty("version"));

        if(!(vFilesManager.checkIfModsExists())){//le dossier mods n'existe pas, donc il faut tout telecharger
            try{
                vFilesManager.createMods();
                String[][] vFilesPath = vFilesManager.readXml(Downloader.downloadFile(vServerConfig.modpackClient(), "Liste des mods",true,"",""));//ici on recupere un tableau 1d car 1seul child element sur le xml
                for(int i = 0; i<vFilesPath.length;i++){
                    Downloader.downloadFile(vServerConfig.modpackClient() + vFilesPath[i][0], vFilesPath[i][0],false,"mods/",vFilesPath[i][0]);
                }
                System.out.println("Mods telecharges, vous pouvez lancer votre jeu.");
                return;
            }catch(Exception pE){
                pE.printStackTrace();
                return;
            }
        }
        else{//Le dossier mods existe il faut mettre à jour seulement les mods obseletent
            try {
                vModsManager = new ModsManager(vFilesManager.listJar(),vFilesManager,vServerConfig);
                vModsManager.printModList();

                String[][] vFilesPath = vFilesManager.readXml(Downloader.downloadFile(vServerConfig.modsJson(), "Liste des mods",true,"",""));//On recuepre les fichier JSON du serveur
                
                System.out.println("Verification mise a jour");
                vModsManager.updateModFiles(vFilesPath);//On lance la methode de maj

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        
    }
}