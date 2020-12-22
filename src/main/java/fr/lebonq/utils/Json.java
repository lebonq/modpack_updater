package fr.lebonq.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Json {

    private Json() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Permet de lire un fichier json
     * 
     * @param pFile
     * @return
     * @throws IOException
     */
    public static String readFile(File pFile) {
        String vBodyJson = "";
        try (FileReader vReader = new FileReader(pFile);){
            int i = vReader.read();
            while(i != -1){
                vBodyJson = vBodyJson.concat("" + (char)i);
                i = vReader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return vBodyJson;
    }
}
