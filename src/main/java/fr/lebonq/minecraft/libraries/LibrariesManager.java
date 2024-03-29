package fr.lebonq.minecraft.libraries;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;
import fr.lebonq.utils.IdentifierToPath;

public class LibrariesManager {

    private LibrariesManager() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Permet de lister toute les libraries necesssaires
     * @param pFileMc le client json
     * @param pFileFabric le clientfabric json
     * @return Une hasset avec tout les objets Librarie necessaire
     */
    public static List<Librarie> downloadLibraries(File pFileMc,File pFileFabric) {
        //For Fabric libraries
        JsonParser vParserFabric = new JsonParser();

        JsonObject vFileFabric = null;
        List<Librarie> vLibrarieVector = new ArrayList<>();//On renvoie un hashset pour pouvoir gerer les natives etc
        try(FileReader vFileFarbicReader = new FileReader(pFileFabric);) {
            vFileFabric = (JsonObject) vParserFabric.parse(vFileFarbicReader);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
        JsonArray vLibrariesFabric = vFileFabric.getAsJsonObject().get("libraries").getAsJsonArray();
        
        for (JsonElement jsonElement : vLibrariesFabric) {
            JsonObject vObj = jsonElement.getAsJsonObject();
            String vPath = IdentifierToPath.convert(vObj.get("name").getAsString());
            String vUrl = vObj.get("url").getAsString() + vPath;
            Librarie vReturn = new Librarie(vPath, "", 0, vUrl,false);
            vLibrarieVector.add(vReturn);
        }
        AppController.LOGGER.log(Level.DEBUG,"{}", vLibrariesFabric);
        
        //For mc libraries
        JsonParser vParser = new JsonParser();

        JsonObject vFile = null;
        try(FileReader vFileMcReader = new FileReader(pFileMc);){
            vFile = (JsonObject) vParser.parse(vFileMcReader);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
        JsonArray vLibraries = vFile.getAsJsonArray("libraries");

        //On pourrais detecter l'os est telecharger les libraries en consequences mais pour l'instant je nai aucune machine ou tester sur Unix our Macos
        //il ne reste plus que a regarder et a ajouter dans les conditions les valeurs de l'os
        int i = 0;
        for (JsonElement jsonElement : vLibraries) {
            JsonObject vObjArtifact = jsonElement.getAsJsonObject().get("downloads").getAsJsonObject().get("artifact").getAsJsonObject();

            JsonElement vElemtRules = jsonElement.getAsJsonObject().get("rules");
            if(vElemtRules == null){
                //Un libraries peut ne peux pas svoir de rules mais etre native a un system donc on double check 
                JsonElement vElmClassifiers = jsonElement.getAsJsonObject().get("downloads").getAsJsonObject().get("classifiers");

                if(vElmClassifiers == null){//Si le classifies n'existe pas c'est quil n'y a pas de natives jar 
                    String vSha1 = vObjArtifact.get("sha1").getAsString();
                    String vPath = vObjArtifact.get("path").getAsString();
                    String vUrl = vObjArtifact.get("url").getAsString();
                    int vSize = vObjArtifact.get("size").getAsInt();
                    Librarie vReturn = new Librarie(vPath, vSha1, vSize, vUrl,true);
                    vLibrarieVector.add(vReturn);

                    i++;
                }//if
                else{//Sinon c'est que c'est une bibliotheque natives

                    String vSha1 = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("sha1").getAsString();
                    String vPath = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("path").getAsString();
                    String vUrl = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("url").getAsString();
                    int vSize = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("size").getAsInt();
                    String vSha1NonNatives = vObjArtifact.get("sha1").getAsString();

                    JsonElement vCheckExtact = jsonElement.getAsJsonObject().get("extract");
                    String[] vExcludeString = null;
                    if(vCheckExtact != null){//On check tout les fichier a exclure de lextraction du native
                        JsonArray vExcludeArray = vCheckExtact.getAsJsonObject().getAsJsonArray("exclude");
                        vExcludeString = new String[vExcludeArray.size()];
                        for (int j = 0; j < vExcludeArray.size(); j++) {
                            vExcludeString[j] = vExcludeArray.get(j).getAsString();
                        }
                    }

                    LibrarieNative vReturn = new LibrarieNative(vPath, vSha1, vSize, vUrl,vExcludeString,true);
                    //On ajoute les antive a la libraries correspondante
                    Iterator<Librarie> vIt = vLibrarieVector.iterator();
                    while (vIt.hasNext()){
                        Librarie vTemp = vIt.next();
                        if(vTemp.equals(vSha1NonNatives)){
                            vTemp.setNativeLibrarie(vReturn);
                            break;
                        }
                    }
                    i++;
                }//else

            }//if
            else{
                if(vElemtRules.getAsJsonArray().get(0).getAsJsonObject().get("action").getAsString().equals("allow") &&// On regarde si le premier champs est egale a allow
                    vElemtRules.getAsJsonArray().get(0).getAsJsonObject().get("os") == null){//Et si le champs os est egale a null sa veut dire que la libriries n'est pas pour osx et donc pour win
                        
                    JsonElement vElmClassifiers = jsonElement.getAsJsonObject().get("downloads").getAsJsonObject().get("classifiers");
                    if(vElmClassifiers == null){//Si le classifies n'existe pas c'est quil n'y a pas de natives jar 

                        String vSha1 = vObjArtifact.get("sha1").getAsString();
                        String vPath = vObjArtifact.get("path").getAsString();
                        String vUrl = vObjArtifact.get("url").getAsString();
                        int vSize = vObjArtifact.get("size").getAsInt();
                        Librarie vReturn = new Librarie(vPath, vSha1, vSize, vUrl,true);
                        vLibrarieVector.add(vReturn);

                        i++;
                            
                    }//if
                    else{//Sinon c'est que c'est une bibliotheque natives et on en re telecharge pas la "normale" car elle sont presente 2 fois
                        String vSha1 = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("sha1").getAsString();
                        String vPath = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("path").getAsString();
                        String vUrl = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("url").getAsString();
                        int vSize = vElmClassifiers.getAsJsonObject().get("natives-windows").getAsJsonObject().get("size").getAsInt();
                        String vSha1NonNatives = vObjArtifact.get("sha1").getAsString();

                        JsonElement vCheckExtact = jsonElement.getAsJsonObject().get("extract");
                        String[] vExcludeString = null;

                        if(vCheckExtact != null){
                            JsonArray vExcludeArray = vCheckExtact.getAsJsonObject().getAsJsonArray("exclude");
                            vExcludeString = new String[vExcludeArray.size()];
                                for (int j = 0; j < vExcludeArray.size(); j++) {
                                vExcludeString[j] = vExcludeArray.get(j).getAsString();
                            }
                        }
        
                        LibrarieNative vReturn = new LibrarieNative(vPath, vSha1, vSize, vUrl,vExcludeString,true);
        
                        Iterator<Librarie> vIt = vLibrarieVector.iterator();
                        while (vIt.hasNext()){
                            Librarie vTemp = vIt.next();
                            if(vTemp.equals(vSha1NonNatives)){
                                    vTemp.setNativeLibrarie(vReturn);                                    
                                    break;
                            }
                        }
                        i++;
                    }//else
                }//if
                else{//Ici pour DL les fichier MACOS

                }//else
            }//else
        }//for
       AppController.LOGGER.log(Level.INFO,"Il y a {} libraries a telecharger", i);

        return vLibrarieVector;
    }//download

}
