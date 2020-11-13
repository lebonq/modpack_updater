package fr.lebonq.remote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import fr.lebonq.AppController;
import fr.lebonq.utils.LoadingAnim;

import org.apache.http.client.config.RequestConfig;

public class Downloader {
    
    /**
     *  * Permet de telecharger le fichier avec lurl passe en parametre
     * @param pUrl Url du fichier a dl
     * @param pNomMod nom du mod pour seul but esthetique
     * @param pTemp pour dire si le fochier telecharger doit etre temporaire ou non
     * @param pPath Effectif que si pTemp = false le chemin ou doit etre enregistre le fichier(doit etre cree au prealable)
     * @param pFileName Effectif que si pTemp = false le nom du fichier a enregistrer
     * @param pController Permet de communiquer avec l"ui
     * @return le fichier telecharge
     */
    public static File downloadFile(String pUrl,String pNameMod,boolean pTemp,String pPath,String pFileName,AppController pController) {
        System.out.println("Connexion..");
        File vDownloadedFile = null;
        
        RequestConfig.Builder vConfigBuilder = RequestConfig.custom();
        vConfigBuilder.setConnectTimeout(30000);
        RequestConfig vConfig = vConfigBuilder.build();

        try(CloseableHttpClient vHttpClient = HttpClients.createDefault();){
            HttpGet vRequest = new HttpGet(pUrl);//On creer la requete
            vRequest.setConfig(vConfig);

            HttpEntity vEntity = vHttpClient.execute(vRequest).getEntity();
            
            if(pTemp){
                vDownloadedFile = File.createTempFile(pUrl,".tmp");
            }else{
                vDownloadedFile = new File(pPath + pFileName);
            }

            InputStream vWebFile = vEntity.getContent();//On recupere le corps du fichier
            long vTotalSize = vEntity.getContentLength();

            try {
                // On crée l'OutputStream vers la sortie
                OutputStream vOutput = new FileOutputStream(vDownloadedFile);
                try {
                // On utilise une lecture bufférisé
                    byte[] vBuf = new byte[1024];
                    int vLen;
                    long VLenC = 0;//En long car sinon on depasse la taille
                    System.out.println("Telechargement de " + pNameMod);
                    pController.setUpdateLabel("Telechargement de " + pNameMod);
                    while ( (vLen=vWebFile.read(vBuf)) > 0 ) {
                        vOutput.write(vBuf, 0, vLen);
                        VLenC+=vLen; //On ajoute la taille du buf lu
                        System.out.print(LoadingAnim.anim((VLenC*100l)/vTotalSize) + "\r");//ON affiche le pourcentage
                        pController.setDownloadProgressbar((double)((VLenC*100l)/vTotalSize)/100);//On met a jour la progressbar
                    }
                    System.out.println("");
                } finally {
                        // Fermeture du fichier de sortie
                        vOutput.close();
                    }
            } finally {
                // Fermeture de l'inputStream en entrée
                vWebFile.close();
                vHttpClient.close();//On ferme le client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vDownloadedFile;
    }
}
