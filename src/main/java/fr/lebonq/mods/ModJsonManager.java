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
        try {
            JsonObject vJsonO = (JsonObject) vJsonP.parse(new FileReader(pFile));//On converti en JsonObject pour avoir acces a tout les champs
       
            String vName = vJsonO.get("name").getAsString();//On recupere la String correpondante
            String vVersion = vJsonO.get("version").getAsString();
            return new String[]{vName,vVersion};
        }catch(Exception pE){
            //Si erreur car Optifine ou pas de fichier Json alors on met le nom du fichier dans name et version
            return new String[]{pFile.getName(),pFile.getName()};
        }
    }
}
