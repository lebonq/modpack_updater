package fr.lebonq.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.lebonq.AppController;
import fr.lebonq.minecraft.Minecraft;
/**
 * Cette classe permet de gerer les fichiers des mods, notamment les .jar
 */
public class FilesManager{
    private File aFolderMods;

    /**
     * Constructeur de notre gestionnaire de fichier
     */
    public FilesManager(Minecraft pMinecraft) {
        this.aFolderMods = new File(pMinecraft.getClientFolder().getAbsolutePath() + "/mods");
        createMods();
    }

    /**
     * Permet de savoir si le dossier mods existe
     * @return boolean
     */
    public boolean checkIfModsExists(){
        return this.aFolderMods.exists();
    }
    /**
     * Permet de creer le dossier mods
     */
    public void createMods(){
        
        if(!checkIfModsExists()){
            AppController.LOGGER.log(Level.INFO,"Création du dossier mods car celui ci est introuvable ou vide.");
            this.aFolderMods.mkdir();
        }
    }

    /**
     * Permet de savoir si le fichier est un .jar
     * @param pFile
     * @return boolean
     */
    public boolean isJar(File pFile) throws IOException{
        if(pFile.isDirectory()){
            return false;
        }
        return this.getExtension(pFile).equals("jar");
    }

    /**
     * Permet de connaitre l'extention d'un fichier
     * @param pFile exemple 'server.jar'
     * @return String  exemple 'jar'
     */
    public String getExtension(File pFile) throws IOException{
        if(pFile.isDirectory()){
            throw new IOException();
        }

        int vIndex = pFile.getName().lastIndexOf('.');
        String vExtension = null;
        if(vIndex > 0) {
            vExtension = pFile.getName().substring(vIndex + 1);
        }
        return vExtension;
    }

    /**
     * Permet de connaitre le nombre de fichier jar dans un dossier
     * @param pFile
     * @return
     */
    public int numberOfJar(File[] pFile) throws IOException{
        int res = 0;

        if(pFile == null) return res;
        
        for(int i = 0; i<pFile.length;i++){
            if(isJar(pFile[i])){
                res++;
            }
        }
        return res;
    }

    /**
     * Creer la liste de tout les fichier jars du dossier mods
     * @return une liste de File
     * @throws Exception Si le dossier mods a un dossier
     */
    public File[] listJar() throws IOException{
        File[] vJarFilesList = this.aFolderMods.listFiles(); //On met la liste de tout les fichiers du dossier mods

        int vNumberOfMods = this.numberOfJar(vJarFilesList);
        File[] vRerturnFiles = new File[vNumberOfMods];

        int j = 0;
        for(int i =0; i < vJarFilesList.length;i++){
            if(isJar(vJarFilesList[i])){
                vRerturnFiles[j] = vJarFilesList[i];//On stocke les fichiers .jar
                j++;
            }
        }
        return vRerturnFiles;
    }

    /**
	 *  Methode qui permet d'extraire le fichier JSON des mods fabrics
	 * @param pFile l'objet File de notre fichier .jar
     * @param pFileToExtract Le fichier a extraire
	 * @return l'objet File de notre fichier fabric.mod.json
	 */
	public File extractFromJar(File pFile,String pFileToExtract){
		File vJsonFile = null;
		try (JarFile vJarFile = new JarFile(pFile);){
            String vName = pFileToExtract;

			//On récupère l'objet ZipEntry correspondant
            ZipEntry vEntry = vJarFile.getEntry(vName);
            
			// On crée un File représentant le fichier  :
            vJsonFile = File.createTempFile("modpackupdater/" + vName, ".tmp"); //On cree un fichier temporaire
            vJsonFile.deleteOnExit(); //On le supprime pour eviter de saturer le stockage du client
            
			// On récupère l'InputStream du fichier à l'intérieur du ZIP/JAR
			try ( InputStream vInput = vJarFile.getInputStream(vEntry);){
				// On crée l'OutputStream vers la sortie
				try (OutputStream vOutput = new FileOutputStream(vJsonFile);){
				   // On utilise une lecture bufférisé
					byte[] vBuf = new byte[4096];
					int vLen;
					while ( (vLen=vInput.read(vBuf)) > 0 ) {
						vOutput.write(vBuf, 0, vLen);
					}
				}
			}
		} catch (NullPointerException pE) {
			AppController.LOGGER.log(Level.INFO,"Fichier {} introuvable", pFileToExtract);
			return pFile;//Si erreur car Optifine ou pas de fichier Json dans le mod on retourne le fichier lui meme
		}catch(Exception pE){
			pE.printStackTrace();
			return null;
		}
		return vJsonFile;
    }
    /**
     * Permet de lire les fichier XML du serveur
     * @param pFile 
     * @return Un tableau 2d avec les balise XML renvoye dans lordre du fichier
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws Exception
     */
    public String[][] readXml(File pFile) throws IOException, SAXException, ParserConfigurationException{
        DocumentBuilderFactory vDbFactory = DocumentBuilderFactory.newInstance();
        vDbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        vDbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        DocumentBuilder vDBuilder = vDbFactory.newDocumentBuilder();
        Document vDocument = vDBuilder.parse(pFile);

        vDocument.getDocumentElement().normalize(); // https://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work

        NodeList vListFile = vDocument.getElementsByTagName("file");
        NodeList vListChild =  vListFile.item(0).getChildNodes();

        int vNbFile = vListFile.getLength();
        int vNbChild = vListChild.getLength();

        String[][] vReturn = new String[vNbFile][vNbChild];

        int i = 0;
        for(i = 0; i < vNbFile; i++){

            for(int j = 0; j < vNbChild;j++){
                vReturn[i][j] = vListFile.item(i).getChildNodes().item(j).getTextContent();
            }

        }
        return vReturn;
    }

    public File getModFolder(){
        return this.aFolderMods;
    }
}
