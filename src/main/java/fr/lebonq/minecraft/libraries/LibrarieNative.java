package fr.lebonq.minecraft.libraries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class LibrarieNative extends Librarie {
    private String[] aExclude;
    private boolean aHasExclude;

    public LibrarieNative(String aPath, String aSha1, int aSize, String aUrl, String[] pExclude,boolean pHasSHA1) {
        super(aPath, aSha1, aSize, aUrl,pHasSHA1);
        this.aExclude = pExclude;
        if (pExclude == null) {
            this.aExclude = new String[0];
            this.aHasExclude = false;
        } else {
            this.aHasExclude = true;
        }
    }

    public String[] getAExclude() {
        return this.aExclude;
    }

    public boolean hasExclude() {
        return this.aHasExclude;
    }

    public void extractNative(File pBin) throws Exception {
        JarFile vJar = new JarFile(this.aFile);
        Enumeration<JarEntry> vEntries = vJar.entries();
        while(vEntries.hasMoreElements()){
            boolean vIsExclude = false;
            JarEntry vEntry = vEntries.nextElement();
            for (int i = 0; i < aExclude.length; i++) {
                if (vEntry.getName().contains(aExclude[i]) && this.aHasExclude) {//On verifie si on doit lexclude
                    vIsExclude = true;
                    System.out.println("on doit me voir 2 fois");
                }                    
            
            }
            if(!(vIsExclude)){
                if(vEntry.isDirectory()){//Si c'est un directory on recupere tout dedans avant d'aller plus loins
                }
                else{
                    File vFileOut = new File(pBin + "/" +vEntry.getName());
                    vFileOut.deleteOnExit();
                    if(vEntry.getName().lastIndexOf("/") != -1){//Si == -1 alors c'est un fichier sans dossier
                        File vFileFolder = new File(pBin + "/" + vEntry.getName().substring(0, vEntry.getName().lastIndexOf("/")));//to create folder without file name
                        vFileFolder.mkdirs();
                        vFileFolder.deleteOnExit();//On supprimer le fichier a la du process launcher
                    }

                    InputStream vInput = vJar.getInputStream(vEntry);
                    OutputStream vOutputStream = new FileOutputStream(vFileOut);
                    // On utilise une lecture bufférisé
                    byte[] vBuf = new byte[4096];
                    int vLen;
                    while ( (vLen=vInput.read(vBuf)) > 0 ) {
                        vOutputStream.write(vBuf, 0, vLen);
                    }
                    vOutputStream.close();
                    vInput.close();
                }
            }//if
        }//while
        vJar.close();        
    }
}
