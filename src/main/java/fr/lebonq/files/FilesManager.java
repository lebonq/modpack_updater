package fr.lebonq.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
/**
 * Cette classe permet de gerer les fichiers des mods, notamment les .jar
 */
public class FilesManager{
    private File aFolderMods;
    private File aFileVersion;

    /**
     * Constructeur de notre gestionnaire de fichier
     */
    public FilesManager() {
        this.aFolderMods = new File("mods");
        this.aFileVersion = new File("mods/.version.txt");
        createMods();
    }

    /**
     * Permet de savoir si le dossier mods existe
     * @return boolean
     */
    public boolean checkIfModsExists(){
        if(!(this.aFolderMods.exists())){//On test avant pour ne pas faire crash le programme
            return false;
        }
        if(this.aFolderMods.delete()){//Si on peut le supprimer c'est que le dossier est vide et donc qu'il n'y a acun mods
            return false;
        }
        return true;
    }
    /**
     * Permet de creer le dossier mods
     */
    public void createMods(){
        if(!checkIfModsExists()){
            System.out.println("Création du dossier mods car celui ci est introuvable ou vide.");
            this.aFolderMods.mkdir();
        }
    }

     /**
     * Permet de savoir si le fichier .version existe
     * @return boolean
     */
    public boolean checkIfVersionExists(){
        return this.aFileVersion.exists();
    }
    /**
     * Permet de savoir si le fichier est un .jar
     * @param pFile
     * @return boolean
     */
    public boolean isJar(File pFile) throws Exception{
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
    public String getExtension(File pFile) throws Exception{
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
    public int numberOfJar(File[] pFile) throws Exception{
        int res = 0;
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
    public File[] listJar() throws Exception{
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
     * Permet de comparer le fichier version passe en parametre avec celui dans le dossier mods
     * @param pFile2
     * @return True si la version est la meme
     * @throws Exception BufferedReader n'arrive pas a ouvrir le fichier
     */
    public boolean compareTxtVersion(File pFile2) throws Exception{
        BufferedReader vBufferedReader1 = new BufferedReader(new FileReader(this.aFileVersion)); 
        BufferedReader vBufferedReader2 = new BufferedReader(new FileReader(pFile2)); 

        String vContent1String = vBufferedReader1.lines().findFirst().get();
        String vContent2String = vBufferedReader2.lines().findFirst().get();

        boolean vReturn = vContent1String.equals(vContent2String);
        vBufferedReader1.close();
        vBufferedReader2.close();
        return vReturn;
    }

    /**
	 *  Methode qui permet d'extraire le fichier JSON des mods fabrics
	 * @param pFile l'objet File de notre fichier .jar
     * @param pFileToExtract Le fichier a extraire
	 * @return l'objet File de notre fichier fabric.mod.json
	 */
	public File extractFromJar(File pFile,String pFileToExtract){
        //System.out.println(pFileToExtract);
		JarFile vJarFile = null;
		File vJsonFile = null;
		try {
			vJarFile = new JarFile(pFile);
			try {
				String vName = pFileToExtract;
				//On récupère l'objet ZipEntry correspondant
				ZipEntry vEntry = vJarFile.getEntry(vName);
				// On crée un File représentant le fichier  :
				
				vJsonFile = File.createTempFile(vName, ".tmp"); //On cree un fichier temporaire
				// On récupère l'InputStream du fichier à l'intérieur du ZIP/JAR
				InputStream vInput = vJarFile.getInputStream(vEntry);
				try {
					// On crée l'OutputStream vers la sortie
					OutputStream vOutput = new FileOutputStream(vJsonFile);
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
			} finally {
				// Fermeture du JarFile
				vJarFile.close();
			}
		} catch (NullPointerException pE) {
			System.out.println("Fichier " + pFileToExtract + " introuvable");
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
     * @throws Exception
     */
    public String[][] readXml(File pFile) throws Exception{
        DocumentBuilderFactory vDbFactory = DocumentBuilderFactory.newInstance();
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
}
