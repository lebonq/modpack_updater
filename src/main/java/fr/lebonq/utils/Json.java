package fr.lebonq.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Json {
    /**
     * Permet de lire un fichier json
     * 
     * @param pFile
     * @return
     * @throws IOException
     */
    public static String readFile(File pFile) throws IOException {
        FileReader vReader = new FileReader(pFile);
            String vBodyJson = "";
            int i = vReader.read();
            while(i != -1){
                vBodyJson = vBodyJson.concat("" + (char)i);
                i = vReader.read();
            }
            vReader.close();
            return vBodyJson;
    }
}
