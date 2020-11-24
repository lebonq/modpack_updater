package fr.lebonq.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.lang3.RandomStringUtils;

import fr.lebonq.AppController;
import fr.lebonq.minecraft.Account.Auth;
import fr.lebonq.minecraft.assets.Asset;
import fr.lebonq.minecraft.assets.AssetsManager;
import fr.lebonq.minecraft.launch.Args;
import fr.lebonq.minecraft.libraries.Librarie;
import fr.lebonq.minecraft.libraries.LibrariesManager;
import fr.lebonq.minecraft.versions_manifest.VersionsFile;
import fr.lebonq.remote.Downloader;

public class Minecraft {
    private String aVersion;
    private File aClientFolder;
    private File aVersionFolder;
    private File aClientJsonCurrentVersion; // Si version 1.6.3 alors 1.6.3.json
    private File aAssetsFolder;
    private File aLibrariesFolder;
    private File aAssetsIndexFile;
    private File aClientJarMc;
    private File aBinFolder;
    private File aDlog4j;
    private Vector<Librarie> aLibraries;
    private Asset[] aAssets;
    private AppController aController;
    private Args aArgs;
    private Process aMcProcess;
    private Fabric aFabric;

    // Compte
    private String aUsernameMc;
    private String aUsername;
    private String aPassword;
    private String aUUID;
    private String aAccesToken;

    public Minecraft(String pRoot, AppController pController) {

        this.aController = pController;
        Scanner pSc = null;
        try {
            pSc = new Scanner(Downloader.downloadFile(pRoot + "version.txt", "Version File", true, "", 0, true,
                    this.aController));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.aVersion = pSc.nextLine();// On recupere la version necessaire

        // On cree le dossier de minecraft
        this.aClientFolder = new File(System.getProperty("user.home") + "/AppData/Roaming/.modpack_updater");// On cree le dossier du client dans le user directory
        this.aClientFolder.mkdirs();

        // On cree le dossier version pour enregistrer le manifest de la version
        this.aVersionFolder = new File(this.aClientFolder.getAbsolutePath() + "/versions/" + this.aVersion);
        this.aVersionFolder.mkdirs();

        // On cree le dossier bin
        String shortId1 = RandomStringUtils.randomAlphanumeric(4).toLowerCase();// On cree un id unique comme le launcher officiel
        String shortId2 = RandomStringUtils.randomAlphanumeric(4).toLowerCase();// Et on met en lower case
        String shortId3 = RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        String shortId4 = RandomStringUtils.randomAlphanumeric(4).toLowerCase();
        this.aBinFolder = new File(this.aClientFolder.getAbsolutePath() + "/bin/" + shortId1 + "-" + shortId2 + "-"
                + shortId3 + "-" + shortId4);
        this.aBinFolder.mkdirs();
        this.aBinFolder.deleteOnExit();

        // On cree le dossier Libraries
        this.aLibrariesFolder = new File(this.aClientFolder.getAbsolutePath() + "/libraries");
        this.aLibrariesFolder.mkdirs();

        // On cree le dossier des assets
        this.aAssetsFolder = new File(this.aClientFolder.getAbsolutePath() + "/assets/");
        File vIndexes = new File(this.aClientFolder.getAbsolutePath() + "/assets/indexes");
        File vObjects = new File(this.aClientFolder.getAbsolutePath() + "/assets/objects");
        vIndexes.mkdirs();
        vObjects.mkdirs();

        this.aFabric = new Fabric();
        try {
            this.aFabric.setFabricJson(Downloader.downloadFile("https://meta.fabricmc.net/v2/versions/loader/1.16.3",
                    "Fabric data", true, "", 0, false, pController));
        } catch (Exception e) {
            this.aController.setUpdateLabel("Erreur donnees Fabricloader");
            e.printStackTrace();
        }
    }

    public void checkGame() {
        
        //On recupere le client.json
        File vVersionManifest = Downloader.downloadFile(RemoteMojangConfig.mcVersionsList.getaLink(), "MC manifest", true, "", 0,true, this.aController);
        VersionsFile vClientJson = ExtractInfoVersionsManifest.extractUrl(this.aVersion, vVersionManifest);
        String vClientJsonUrl = vClientJson.retriveUrlFromVersion(this.aVersion);
        this.aClientJsonCurrentVersion = Downloader.downloadFile(vClientJsonUrl, "Fichier de version :" + this.aVersion, false, this.aVersionFolder.getAbsolutePath() + "/", 0,true, this.aController);
        
        //On dl Dlog4j
        this.aDlog4j = GetDlog4j.download(this.aClientJsonCurrentVersion, this.aAssetsFolder.getAbsolutePath() + "/", this.aController);

        //On telecharge le client
        this.aClientJarMc = ClientJar.downloadJar(this.aClientJsonCurrentVersion, this.aVersionFolder.getAbsolutePath() + "/", this.aController,this.aVersion);
        //On renomme le fichier et le reafecte au bon emplacement
        File VMcJarTemps = new File(this.aVersionFolder.getAbsolutePath() + "/" +this.aVersion + ".jar");
        this.aClientJarMc.renameTo(VMcJarTemps);//On renomme le fichier jar
        this.aClientJarMc = VMcJarTemps;

        try {
			this.aFabric.getZipFabric(this.aVersion,this.aVersionFolder,this.aClientJarMc, this.aController);
		} catch (Exception e) {
			this.aController.setUpdateLabel("Erreur dans le telechargement de Minecraft");
			e.printStackTrace();
		}//Permet de rcup le json et de mettre le jar

        //On recupere lindex d'assets
        String vAssetsIndexUrl = AssetsManager.urlAssetsIndex(this.aClientJsonCurrentVersion);
        
        this.aAssetsIndexFile = Downloader.downloadFile(vAssetsIndexUrl, "Assets Index", false, this.aAssetsFolder.getAbsolutePath() + "/indexes/", 0,true, this.aController);
        downloadAssets();


        //Dl libraries
        this.aLibraries = LibrariesManager.downloadLibraries(this.aClientJsonCurrentVersion,this.aFabric.getAFabricVersionJson());
        Iterator<Librarie> vIt = this.aLibraries.iterator();
        int i = 0;
        while(vIt.hasNext()){
            Librarie vTemp = vIt.next();
            i++;
            this.aController.setDownloadProgressbar((double)((i*100l)/this.aLibraries.size())/100);
            vTemp.download(this.aLibrariesFolder.getAbsolutePath(), this.aController);
            vTemp.extractNatives(this.aBinFolder);
        }
    }

    public void launch(){
        //On cree les argument pour lancer le jeu
        this.aArgs = new Args();
        this.aArgs.setArg2();
        this.aArgs.setArg3();
        this.aArgs.setArg5(this.aBinFolder.getAbsolutePath());
        this.aArgs.setArg6("Quent_Launcher");
        this.aArgs.setArg7("3.0");
        this.aArgs.setArg8(this.aLibraries, this.aFabric.getClientJar(), this.aLibrariesFolder);
        this.aArgs.setArg16(this.aDlog4j);
        this.aArgs.setArg17(this.aUsernameMc);
        this.aArgs.setArg18(this.aVersion);
        this.aArgs.setArg19(this.aClientFolder);
        this.aArgs.setArg20(this.aAssetsFolder);
        this.aArgs.setArg21(this.aVersion.substring(0, this.aVersion.lastIndexOf(".")));
        this.aArgs.setArg22(this.aUUID);
        this.aArgs.setArg23(this.aAccesToken);   
        
        System.out.println(this.aArgs.getCommand());
        try {
            this.aMcProcess = Runtime.getRuntime().exec(this.aArgs.getCommand(),null,this.aClientFolder);
            
            //System.out.println(vProcess.pid());

            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(this.aMcProcess.getInputStream()));
            String readLine;
            this.aController.setUpdateLabel("Minecraft est lance");
            while ((readLine = processOutputReader.readLine()) != null)
            {
                System.out.println(readLine + System.lineSeparator());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //this.aBinFolder.delete();
    }

    private void downloadAssets(){
        this.aAssets = AssetsManager.getAssets(this.aAssetsIndexFile);
        int i = 0;
        for (Asset asset : aAssets) {
            i++;
            this.aController.setDownloadProgressbar((double)((i*100l)/this.aAssets.length)/100);
            asset.createFolderDownload(this.aAssetsFolder.getAbsolutePath() + "/objects/", this.aController);
        }
    }

    public void login() throws Exception{
        String[] vResponse = new String[3];
        vResponse = Auth.authenticate(this.aUsername, this.aPassword);
        this.aUsernameMc = vResponse[2];
        this.aUUID = vResponse[1];
        this.aAccesToken =vResponse[0];
    }
   
    public File getClientFolder(){
        return this.aClientFolder;
    }


    public String getUsernameMc() {
        return this.aUsernameMc;
    }

    public void setUsernameMc(String aUsernameMc) {
        this.aUsernameMc = aUsernameMc;
    }

    public String getUsername() {
        return this.aUsername;
    }

    public void setUsername(String aUsername) {
        this.aUsername = aUsername;
    }

    public void setPassword(String aPassword) {
        this.aPassword = aPassword;
    }

}
