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

import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.GetSha1;

public class GetDlog4j {

    private GetDlog4j(){

    }

    public static File download(File pFile, String pPath,AppController pController) {
        JsonParser vParser = new JsonParser();

        JsonObject vFile = null;
        try {
            vFile = (JsonObject) vParser.parse(new FileReader(pFile));
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }

        JsonObject vFileObj = vFile.get("logging").getAsJsonObject().get("client").getAsJsonObject().get("file")
                .getAsJsonObject();
        File vFileCheck = new File(pPath + "log_configs/" + vFileObj.get("id").getAsString());
        if (vFileCheck.exists()) {
            try {
                if (GetSha1.calcSHA1(vFileCheck).equals(vFileObj.get("sha1").getAsString())) {
                    AppController.LOGGER.log(Level.INFO,"SHA1 valide pour le logger");
                    return vFileCheck;
                }
                else{
                    if(!(vFileCheck.delete())){
                        throw new IOException();
                    }
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
        File vFolder = new File(pPath + "log_configs/");
        vFolder.mkdirs();
        Downloader.downloadFile(vFileObj.get("url").getAsString(), vFileObj.get("id").getAsString(), false, pPath+ "log_configs/", vFileObj.get("size").getAsInt(),true, pController);
        return vFileCheck;
    }
}
