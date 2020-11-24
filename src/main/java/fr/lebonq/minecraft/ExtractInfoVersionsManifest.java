package fr.lebonq.minecraft;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

import fr.lebonq.minecraft.versions_manifest.VersionsFile;

public class ExtractInfoVersionsManifest {
    /**
     * Permet de recupere le lien e la version
     * @param pVersion verisonn du jeu      
     * @param pFile Le fichier json
     * @return
     */
    public static VersionsFile extractUrl(String pVersion, File pFile) {
        Gson gson = new Gson(); 
        VersionsFile vVersions = null;
        try {
            vVersions = gson.fromJson(new FileReader(pFile), VersionsFile.class);
        } catch (Exception pE){
            pE.printStackTrace();
        }

        return vVersions;
    }
}
