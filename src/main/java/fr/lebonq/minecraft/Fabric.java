package fr.lebonq.minecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;//pour java 14
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import fr.lebonq.AppController;
import fr.lebonq.remote.Downloader;

public class Fabric {

    private File aJsonFile;
    private String aVersionFabric;
    private File aFabricJar;
    private File aFabricVersionJson;

    public void setFabricJson(File pFile) throws Exception{
        this.aJsonFile = pFile;
        setVersion();
        System.out.println("On utilisera la version " + this.aVersionFabric + " de fabric");
    }

    private void setVersion(){
        JsonParser vParser = new JsonParser();

        JsonArray vFile = null;
        try {
            vFile = (JsonArray) vParser.parse(new FileReader(this.aJsonFile));
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonElement vLatest = vFile.get(0);
        this.aVersionFabric = vLatest.getAsJsonObject().get("loader").getAsJsonObject().get("version").getAsString();
    }

    public void getZipFabric(String pMcVersion,File pVersionPath,File pMcJar,AppController pAppController) throws Exception{
        File vTemp = Downloader.downloadFile("https://meta.fabricmc.net//v2/versions/loader/" + pMcVersion + "/" + this.aVersionFabric + "/profile/zip", "Fabric", true, "", 0, true, pAppController);
        ZipFile vTempZip = new ZipFile(vTemp);
        Enumeration<? extends ZipEntry> vEntries = vTempZip.entries();
        //Iterator<? extends ZipEntry> vIt = vEntries.asIterator(); //incompatible avec java 8
        while(vEntries.hasMoreElements()){
            ZipEntry vNext = vEntries.nextElement();
            System.out.println(vNext.getName());

            File vNextFolder = new File(pVersionPath.getAbsolutePath().substring(0, pVersionPath.getAbsolutePath().lastIndexOf("\\")) + "/"  + vNext.getName().substring(0,  vNext.getName().lastIndexOf("/")));
            vNextFolder.mkdirs();
            File vNextFile = new File(pVersionPath.getAbsolutePath().substring(0, pVersionPath.getAbsolutePath().lastIndexOf("\\")) + "/" + vNext.getName()); //On cree un fichier temporaire
            if(!(vEntries.hasMoreElements())){
                if(vNextFile.exists()){
                    this.aFabricJar = vNextFile;
                }
                else{
                    this.aFabricJar = pMcJar;//Le jar de fabric ne doit pas etre le Dummy jar mais le jar de minecraft
                }
            }
            else{
                this.aFabricVersionJson = vNextFile;
                // On récupère l'InputStream du fichier à l'intérieur du ZIP/JAR
                
                InputStream vInput = vTempZip.getInputStream(vNext);
                try {
                    // On crée l'OutputStream vers la sortie
                    OutputStream vOutput = new FileOutputStream(vNextFile);
                    try {
                    // On utilise une lecture bufférisé
                        byte[] vBuf = new byte[4096];
                        int vLen;
                        while ( (vLen=vInput.read(vBuf)) > 0 ) {
                            vOutput.write(vBuf, 0, vLen);
                        }
                    } finally {
                            // Fermeture du fichier de sortie
                        vOutput.close();
                        }
                } finally {
                    // Fermeture de l'inputStream en entrée
                    vInput.close();
                }
            }
        }

        vTempZip.close();        
    }

    public File getAFabricVersionJson() {
        return this.aFabricVersionJson;
    }


    public File getClientJar() {
        return this.aFabricJar;
    }


}
