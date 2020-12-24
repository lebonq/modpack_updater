package fr.lebonq.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;
import fr.lebonq.minecraft.account.Auth;
import fr.lebonq.minecraft.account.Reminder;
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
    private List<Librarie> aLibraries;
    private Asset[] aAssets;
    private AppController aController;
    private Args aArgs;
    private Process aMcProcess;
    private Fabric aFabric;
    private Reminder aReminder;

    // Compte
    private String aUsernameMc;
    private String aUsername;
    private String aPassword;
    private String aUUID;
    private String aAccesToken;
    private String aClientToken;

    public Minecraft(String pRoot, AppController pController) {

        this.aController = pController;
        try(Scanner pSc = new Scanner(Downloader.downloadFile(pRoot + "version.txt", "Version File", true, "", 0, true,this.aController));){
            this.aVersion = pSc.nextLine();// On recupere la version necessaire
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
        this.aFabric.setFabricJson(Downloader.downloadFile("https://meta.fabricmc.net/v2/versions/loader/" + this.aVersion,"Fabric data", true, "", 0, false, pController));

        this.aReminder = new Reminder(this.aClientFolder);
        if(this.aReminder.hasReminder() && !(this.aReminder.tokenExpires())){//Si il y a un reminder on le charge
            loadReminder();
            this.aController.fillEmailPassword(this.aReminder.getSavedUsername());
            this.aController.checkRemind(true);
        }
    }

    public void checkGame() {
        
        //On recupere le client.json
        File vVersionManifest = Downloader.downloadFile(RemoteMojangConfig.MCVERSIONSLIST.getaLink(), "MC manifest", true, "", 0,true, this.aController);
        VersionsFile vClientJson = ExtractInfoVersionsManifest.extractUrl(vVersionManifest);
        String vClientJsonUrl = vClientJson.retriveUrlFromVersion(this.aVersion);
        this.aClientJsonCurrentVersion = Downloader.downloadFile(vClientJsonUrl, "Fichier de version :" + this.aVersion, false, this.aVersionFolder.getAbsolutePath() + "/", 0,true, this.aController);
        
        //On dl Dlog4j
        this.aDlog4j = GetDlog4j.download(this.aClientJsonCurrentVersion, this.aAssetsFolder.getAbsolutePath() + "/", this.aController);

        //On telecharge le client
        this.aClientJarMc = ClientJar.downloadJar(this.aClientJsonCurrentVersion, this.aVersionFolder.getAbsolutePath() + "/", this.aController,this.aVersion);
        //On renomme le fichier et le reafecte au bon emplacement
        File vMcJarTemps = new File(this.aVersionFolder.getAbsolutePath() + "/" +this.aVersion + ".jar");
        this.aClientJarMc.renameTo(vMcJarTemps);//On renomme le fichier jar
        this.aClientJarMc = vMcJarTemps;

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
            this.aController.setDownloadProgressbar(((i*100d)/this.aLibraries.size())/100d);
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
        this.aArgs.setArg8(this.aLibraries, this.aFabric.getClientJar());
        this.aArgs.setArg16(this.aDlog4j);
        this.aArgs.setArg17(this.aUsernameMc);
        this.aArgs.setArg18(this.aVersion);
        this.aArgs.setArg19(this.aClientFolder);
        this.aArgs.setArg20(this.aAssetsFolder);
        this.aArgs.setArg21(this.aVersion.substring(0, this.aVersion.lastIndexOf(".")));
        this.aArgs.setArg22(this.aUUID);
        this.aArgs.setArg23(this.aAccesToken);   
        
        AppController.LOGGER.log(Level.DEBUG,"Command use : {}",this.aArgs.getCommand());
        try {
            this.aMcProcess = Runtime.getRuntime().exec(this.aArgs.getCommand(),null,this.aClientFolder);
            

            BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(this.aMcProcess.getInputStream()));
            String readLine;
            this.aController.setUpdateLabel("Minecraft est lance");
            //On rentre dans la boucle du jeu
            while ((readLine = processOutputReader.readLine()) != null)
            {
                System.out.println(readLine + System.lineSeparator());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }//Si on ressort le jeu est stoppe
        
        //Permet de supprimer tout le dossier bin lorsque le jeu s'arrete
        File[] vToBeDelete = this.aBinFolder.listFiles();
        deleteBinForThisInstance(vToBeDelete);
        
    }

    private void downloadAssets(){
        this.aAssets = AssetsManager.getAssets(this.aAssetsIndexFile);
        int i = 0;
        for (Asset asset : aAssets) {
            i++;
            this.aController.setDownloadProgressbar(((i*100d)/this.aAssets.length)/100d);
            asset.createFolderDownload(this.aAssetsFolder.getAbsolutePath() + "/objects/", this.aController);
        }
    }
    /**
     * permet de se connecter au serveur de mojang et de verifier le compte
     * @return pRemind le boolean si luser veut rester co
     * @throws Exception
     */
    public void login(boolean pRemind) throws IOException{
        if(pRemind && this.aReminder.hasReminder() && !(this.aReminder.tokenExpires())){
            if(Auth.validate(this.aAccesToken, this.aClientToken)){
                AppController.LOGGER.log(Level.INFO,"Access token still valid.");
                return;
            }
            else{
                this.aAccesToken = Auth.refresh(this.aAccesToken, this.aClientToken);
                AppController.LOGGER.log(Level.INFO,"Access token still need to be refresh.");
                this.aReminder.saveRemind(this.aAccesToken, this.aUUID, this.aUsernameMc, this.aUsername, this.aClientToken);
                return;
            }
        }else{
        String[] vResponse = Auth.authenticate(this.aUsername, this.aPassword, null);
        AppController.LOGGER.log(Level.INFO,"Get access token.");
        this.aUsernameMc = vResponse[2];
        this.aUUID = vResponse[1];
        this.aAccesToken = vResponse[0];
        this.aClientToken = vResponse[3];
        }
        if(pRemind)this.aReminder.saveRemind(this.aAccesToken, this.aUUID, this.aUsernameMc, this.aUsername, this.aClientToken);
    }

    private void deleteBinForThisInstance(File[] pFiles){
        for (File file : pFiles) {
            if(file.isDirectory()){
                deleteBinForThisInstance(file.listFiles());
            }
            file.delete();
        }
    }

    public void loadReminder(){
        this.aReminder.loadRemind();
        this.aUsername = this.aReminder.getSavedUsername();
        this.aUUID = this.aReminder.getSavedUUID();
        this.aAccesToken = this.aReminder.getSavedToken();
        this.aUsernameMc = this.aReminder.getSavedDisplayName();
        this.aClientToken = this.aReminder.getSavedClientToken();
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
