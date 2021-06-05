package fr.lebonq.minecraft.assets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class AssetsManager {

    private AssetsManager(){
    }

    /**
     * Permet de recupere le lien de l'index des assets de la verison
     * @param pFile
     * @return
     */
    public static String urlAssetsIndex(File pFile){
        JsonParser vJp = new JsonParser();
        JsonObject vObj = null;
        
        try(FileReader vFileReader = new FileReader(pFile);){
            vObj = (JsonObject) vJp.parse(vFileReader);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
        
        
        JsonObject vAssetIndex = (JsonObject) vObj.get("assetIndex");
        String vUrlAssetsIndex =  vAssetIndex.get("url").getAsString();

        return (vUrlAssetsIndex);
    }

    /**
     * Cree un tableau avec tout les assets requits
     * @param pFile
     * @return
     */
    public static Asset[] getAssets(File pFile){
        JsonParser vJp = new JsonParser();
        JsonObject vObj = null;

        try(FileReader vFileReader = new FileReader(pFile);){
            vObj = (JsonObject) vJp.parse(vFileReader);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
        
        JsonObject vObjects = vObj.getAsJsonObject("objects");
        Set<Entry<String, JsonElement>> vSet = vObjects.entrySet();//On recupere le tout sous forme d;une hashmap

        Asset[] vReturn = new Asset[vSet.size()];//On cree notre tableau d'assets

        Iterator<Entry<String,JsonElement>> vIterator = vSet.iterator();//On cree notre iterateur

        int i = 0;

        while(vIterator.hasNext()){
            Entry<String, JsonElement> vEntry = vIterator.next();
            String vName = vEntry.getKey();//On recupere la cle de l'entry cad le nom du fichier de l'asset
            String vHash = vEntry.getValue().getAsJsonObject().get("hash").getAsString();//On recupere le json element quon converti en JSonobject puis on recupere en string le champs "hash"
            int vSize = vEntry.getValue().getAsJsonObject().get("size").getAsInt();
            vReturn[i] = new Asset(vName, vHash, vSize);
            i++;
        }

        return vReturn;
    }
}
