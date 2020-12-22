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
import org.apache.logging.log4j.Level;

import fr.lebonq.AppController;

import org.apache.http.client.config.RequestConfig;

public class Downloader {
    
    private Downloader(){
    }

    /**
     *  * Permet de telecharger le fichier avec lurl passe en parametre
     * @param pUrl Url du fichier a dl
     * @param pName nom du mod pour seul but esthetique
     * @param pTemp pour dire si le fochier telecharger doit etre temporaire ou non
     * @param pPath Effectif que si pTemp = false le chemin ou doit etre enregistre le fichier(doit etre cree au prealable) ave cun / a la fin
     * @param pSize  = 0 si on ne connais pas a lavance la taille
     * @param pController Permet de communiquer avec l"ui
     * @return File le fichier telecharge
     */
    public static File downloadFile(String pUrl,String pName,boolean pTemp,String pPath,int pSize,boolean pProgressBar,AppController pController) {
        AppController.LOGGER.log(Level.INFO,"Connexion..");
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
                vDownloadedFile = new File(pPath + pUrl.substring(pUrl.lastIndexOf("/"))); // Permet de mettre le nom du fichier distant
                AppController.LOGGER.log(Level.INFO,"{}",pUrl);
                AppController.LOGGER.log(Level.INFO,"{}",vDownloadedFile.getAbsolutePath());
            }

            
            long vTotalSize;
            if(pSize == 0) {vTotalSize = vEntity.getContentLength();}
            else{vTotalSize = pSize;}

            try (InputStream vWebFile = vEntity.getContent();) {//On recupere le corps du fichier
                // On crée l'OutputStream vers la sortie
                try(OutputStream vOutput = new FileOutputStream(vDownloadedFile);) {
                // On utilise une lecture bufférisé
                    byte[] vBuf = new byte[1024];
                    int vLen;
                    long vLenC = 0;//En long car sinon on depasse la taille
                    AppController.LOGGER.log(Level.INFO,"Telechargement de {}", pName);
                    pController.setUpdateLabel("Telechargement de " + pName);
                    while ( (vLen=vWebFile.read(vBuf)) > 0 ) {
                        vOutput.write(vBuf, 0, vLen);
                        vLenC+=vLen; //On ajoute la taille du buf lu
                        //System.out.print(LoadingAnim.anim((vLenC*100l)/vTotalSize) + "\r");//ON affiche le pourcentage
                        if(pProgressBar) pController.setDownloadProgressbar(((vLenC*100d)/vTotalSize)/100d);//On met a jour la progressbar
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vDownloadedFile;
    }
}
