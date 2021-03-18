package fr.lebonq.minecraft;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

import fr.lebonq.minecraft.versions_manifest.VersionsFile;

public class ExtractInfoVersionsManifest {

    private ExtractInfoVersionsManifest(){
    }
    /**
     * Permet de recupere le lien e la version   
     * @param pFile Le fichier json
     * @return
     */
    public static VersionsFile extractUrl( File pFile) {
        Gson gson = new Gson(); 
        VersionsFile vVersions = null;
        try(FileReader vFileReader = new FileReader(pFile);){
            vVersions = gson.fromJson(vFileReader, VersionsFile.class);
        } catch (Exception pE){
            pE.printStackTrace();
        }

        return vVersions;
    }
}
