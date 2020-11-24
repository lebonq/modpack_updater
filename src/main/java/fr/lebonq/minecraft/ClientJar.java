package fr.lebonq.minecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import fr.lebonq.AppController;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.GetSha1;

public class ClientJar {
    /**
     * Permet de dl le client.jar ou de check le sha1
     * @param pFile le client json
     * @param pPath le path vers le dossier de la version
     * @param pController
     * @return
     */
    public static File downloadJar(File pFile, String pPath, AppController pController,String pVerison) {
        JsonParser vJp = new JsonParser();
        JsonObject vObj = null;
   
        try {
            vObj = (JsonObject) vJp.parse(new FileReader(pFile));
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e1) {
            e1.printStackTrace();
        }


        JsonObject vDownloads = (JsonObject) vObj.get("downloads");
        JsonObject vClient = (JsonObject) vDownloads.get("client");

        String vSha1 = vClient.get("sha1").getAsString();
        int vSize = vClient.get("size").getAsInt();
        String vUrl = vClient.get("url").getAsString();

        File vClientJar = new File(pPath + pVerison +".jar");

        if ((checkSha1(vSha1, vClientJar)) && vClientJar.exists()) { //On verifie si le SHA1 est bon et si le document existe
            return vClientJar;
        }

        vClientJar.delete();

        File vRetun = Downloader.downloadFile(vUrl, "de Minecraft", false, pPath, vSize,true, pController);
        
        if (checkSha1(vSha1, vRetun)) { 
             return vRetun;
        }
        return vRetun;
    }

    private static boolean checkSha1(String pSha1, File pFile){
        try {
            if (GetSha1.calcSHA1(pFile).toLowerCase().equals(pSha1)) {
                System.out.println("SHA1 verifie! Pour " + pFile.getName()); 
                return true;
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            return false;
        }
        return false;
    }
}
