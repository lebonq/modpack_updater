package fr.lebonq;

import java.net.URL;
import java.util.ResourceBundle;

import org.aeonbits.owner.ConfigFactory;

import fr.lebonq.files.FilesManager;
import fr.lebonq.mods.ModsManager;
import fr.lebonq.remote.Downloader;
import fr.lebonq.utils.ConfigApp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class AppController implements Initializable{//Permet de mettre a jour certain item a linisiatliation
    private FilesManager aFilesManager;
    private ModsManager aModsManager;
    private ConfigApp aServerConfig;
    private Task aUpdateTask;
    @FXML
    private ProgressBar aDownloadProgress;
    @FXML
    private Label aUpdateLabel;
    @FXML
    private Button aUpdateButton;
    @FXML
    private Label aVersionLabel;
    private String aVersionString;
    @FXML
    private Label aNameOfModpackLabel;
    private String aNameOfModpackString;

    public AppController(String pVersion) throws Exception {
        this.aServerConfig = ConfigFactory.create(ConfigApp.class);
        this.aFilesManager = new FilesManager();
        this.aVersionString = pVersion;
        this.aNameOfModpackString = this.aServerConfig.name();
    }
    
    @Override
	public void initialize(URL location, ResourceBundle resources) { 
        Platform.runLater(() -> this.aVersionLabel.setText(this.aVersionString));//Permet d'afficher la version
        Platform.runLater(() -> this.aNameOfModpackLabel.setText(this.aNameOfModpackString));//Le nom du modpack
    }
    
    /**
     * Permet de changer le text afficher par le label
     * @param pString
     */
    public void setUpdateLabel(String pString){
        Platform.runLater ( () -> this.aUpdateLabel.setText(pString));//permet de mettre a jours les items depusi  nimportquel thread 
    }

    public void setDownloadProgressbar(double pDouble){
        Platform.runLater ( () -> this.aDownloadProgress.setProgress(pDouble));
    }

    @FXML
    public void updateGridPaneSize(ActionEvent pEvent){//Appeler uniquement lors

    }

    @FXML
    public void updateTaskLaunch(ActionEvent pEvent){
        this.aUpdateTask = new Task<Integer>() {
            @Override
            public Integer call() throws Exception {
                aUpdateButton.setDisable(true);//On empeche les autres interaction avec le bouton pour lancer que 1 thread de maj
                update();
                aUpdateButton.setDisable(false);
                return 0;
              }
            };
          Thread th = new Thread(this.aUpdateTask);
          th.setDaemon(true);
          th.start();
    }

    public void update(){
        
        if(!(aFilesManager.checkIfModsExists())){//le dossier mods n'existe pas, donc il faut tout telecharger
                try{
                    setUpdateLabel("Creation du dossier mods");
                    aFilesManager.createMods();
                    String[][] vFilesPath = aFilesManager.readXml(Downloader.downloadFile(this.aServerConfig.modpackClient(), "Liste des mods",true,"","",this));//ici on recupere un tableau 1d car 1seul child element sur le xml
                    for(int i = 0; i<vFilesPath.length;i++){
                        Downloader.downloadFile(this.aServerConfig.modpackClient() + vFilesPath[i][0], vFilesPath[i][0],false,"mods/",vFilesPath[i][0],this);
                    }
                    System.out.println("Mods telecharges, vous pouvez lancer votre jeu.");
                    setUpdateLabel("Mods telecharges, vous pouvez lancer votre jeu.");
                    
                }catch(Exception pE){
                    pE.printStackTrace();
                    return;
                }
        }
        else{//Le dossier mods existe il faut mettre à jour seulement les mods obseletent
            try {
                this.aModsManager = new ModsManager(this.aFilesManager.listJar(),this.aFilesManager,this.aServerConfig,this);
                this.aModsManager.printModList();

                String[][] vFilesPath = this.aFilesManager.readXml(Downloader.downloadFile(this.aServerConfig.modsJson(), "Liste des mods",true,"","",this));//On recuepre les fichier JSON du serveur
                    
                System.out.println("Verification mise à jour");
                setUpdateLabel("Verification mise à jour");
                this.aModsManager.updateModFiles(vFilesPath);//On lance la methode de maj

                System.out.println("Tout les mods sont à jour");
                setUpdateLabel("Tout les mods sont à jour");
                setDownloadProgressbar(1);//Affiche une barre pleine a la fin

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }	
}
