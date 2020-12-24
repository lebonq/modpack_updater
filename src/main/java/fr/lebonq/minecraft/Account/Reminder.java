package fr.lebonq.minecraft.account;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.Level;
import org.shanerx.mojang.Mojang;

import fr.lebonq.AppController;
import fr.lebonq.utils.Json;

public class Reminder {

    private boolean aTokenExpires;
    private File aClientFolder;
    private String aSavedToken;
    private String aSavedDisplayName;
    private String aSavedUUID;
    private String aSavedUsername;
    private String aSavedClientToken;
    private File aSettingsFile;

    public Reminder(File pClientFolder) {
        this.aClientFolder = pClientFolder;
        File vUsercache = new File(this.aClientFolder.getAbsolutePath() + "/usercache.json");

        this.aSettingsFile = new File(this.aClientFolder.getAbsolutePath() + "/launcher_settings.json");

        JsonParser vParser = new JsonParser();
        String vBodyJson = Json.readFile(vUsercache);
        if(vBodyJson == null){
            AppController.LOGGER.log(Level.ERROR, "Pas de fichier token trouvé");
            this.aTokenExpires = false;//Si user cache inexistant alors juste on dit que le token nexprire pas
            return;
        }
        JsonArray vContent = vParser.parse(vBodyJson).getAsJsonArray();
        String vDate = vContent.get(0).getAsJsonObject().get("expiresOn").getAsString();
        SimpleDateFormat vDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss Z");// On defini le format de la date
        Date vDateDate = null;
        try {
            vDateDate = vDateFormat.parse(vDate);
        } catch (ParseException e) {
            this.aTokenExpires = false;
        }
        if (vDateDate.compareTo(new Date()) < 0) {
            AppController.LOGGER.log(Level.WARN,"Le token d'acces n'est plus valide nous devons en recuperer un nouveau");
            this.aTokenExpires = true;

        } else {
            this.aTokenExpires = false;
        }
    }

    /**
     * Permet de sauvegarder lorsque que lutilisateur coche reminder
     * @param pToken
     * @param pUUID
     * @param pUsernameMC
     * @param pUsername
     */
    public void saveRemind(String pToken,String pUUID,String pUsernameMC,String pUsername,String pClientToken){
        Map<String, String> vSettings = new HashMap<>();
        vSettings.put("accessToken", pToken);
        vSettings.put("UUID", pUUID);
        vSettings.put("displayName", pUsernameMC);
        vSettings.put("username", pUsername);
        vSettings.put("clientToken", pClientToken);

        Writer vWriter = null;
        try {
            vWriter = new FileWriter(this.aSettingsFile);
            new Gson().toJson(vSettings, vWriter);
            vWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Permet de charger les donnees stocke dans le fichier
     * 
     * @throws IOException
     */
    public void loadRemind(){
        File vLauncherSettings = new File(this.aClientFolder.getAbsolutePath() + "/launcher_settings.json");
        JsonParser vParser = new JsonParser();

        //Permet de lire proprement un fichier JSON sans le laisser comme jai fait partout ailleurs MDR
        String vBodyJson = Json.readFile(vLauncherSettings);

        this.aSavedToken = vParser.parse(vBodyJson).getAsJsonObject().get("accessToken").getAsString();//Access token
        this.aSavedDisplayName = vParser.parse(vBodyJson).getAsJsonObject().get("displayName").getAsString();
        this.aSavedUUID = vParser.parse(vBodyJson).getAsJsonObject().get("UUID").getAsString();
        this.aSavedUsername = vParser.parse(vBodyJson).getAsJsonObject().get("username").getAsString();
        this.aSavedClientToken = vParser.parse(vBodyJson).getAsJsonObject().get("clientToken").getAsString();
    }
    
    /**
     * Verifie si le access token est valide
     * @return
     */
    public boolean tokenExpires(){
        return this.aTokenExpires;
    }

    public String getSavedDisplayName() {
        return this.aSavedDisplayName;
    }

    public String getSavedUUID() {
        return this.aSavedUUID;
    }

    public String getSavedClientToken() {
        return this.aSavedClientToken;
    }

    public String getSavedUsername() {
        return this.aSavedUsername;
    }

    public File getLauncherSettings(){
        return this.aSettingsFile;
    }

    public String getSavedToken(){
        return this.aSavedToken;
    }

    /**
     * On regarde si le fichier existe
     */
    public boolean hasReminder(){
        return this.aSettingsFile.exists();
    }

    /**
     * Regarde si les donnees save sont bonnes
     * @return
     */
    public boolean checkSaved(){
        Mojang vApi =  new Mojang().connect();
        return vApi.getUUIDOfUsername(this.aSavedDisplayName).equals(this.aSavedUUID);
    }
}
