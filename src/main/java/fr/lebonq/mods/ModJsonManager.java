package fr.lebonq.mods;

import java.io.File;
import java.io.FileReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ModJsonManager {

    /**
     * Permet d'extraire les infromations du fichier JSON
     * @param pFile Notre fichier JSON
     * @return String[] Contenant Nom du mod, Version
     */
    public static String[] getModInfo(File pFile) {
        JsonParser vJsonP = new JsonParser(); //On cree le parswer
        String vName = null;//On le cree en amont pout retourner null sur le champs n'existe aps dans le JSON
        String vVersion = null;
        String vImage = null;
        String vDescription = null;
        try {
            JsonObject vJsonO = (JsonObject) vJsonP.parse(new FileReader(pFile));//On converti en JsonObject pour avoir acces a tout les champs
       
            vName = vJsonO.get("name").getAsString();//On recupere la String correpondante
            vVersion = vJsonO.get("version").getAsString();
            vImage = vJsonO.get("icon").getAsString();
            vDescription = vJsonO.get("description").getAsString();
            return new String[]{vName,vVersion,vImage,vDescription};
        }catch(Exception pE){
            return new String[]{vName,vVersion,vImage,vDescription};
        }
    }
}
