package fr.lebonq;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

public class AppController implements Initializable {// Permet de mettre a jour certain item a linisiatliation
    private FilesManager aFilesManager;
    private ModsManager aModsManager;
    private ConfigApp aServerConfig;
    private Task<Integer> aUpdateTask;
    private int aNbRows;
    final private int aNbColumns = 3;
    final private int aGap = 5;

    @FXML
    private ProgressBar aDownloadProgress;

    @FXML
    private Label aUpdateLabel;

    @FXML
    private Button aUpdateButton;

    @FXML
    private Button aUpdateListButton;

    @FXML
    private Label aVersionLabel;
    private String aVersionString;

    @FXML
    private Label aLittleUpdateLabel;

    @FXML
    private Label aNameOfModpackLabel;
    private String aNameOfModpackString;

    @FXML
    private TilePane aListMods;

    public AppController(String pVersion) throws Exception {
        this.aServerConfig = ConfigFactory.create(ConfigApp.class);
        this.aFilesManager = new FilesManager();
        this.aVersionString = pVersion;
        this.aNameOfModpackString = this.aServerConfig.name();
    }

    /**
     * Se lance juste apres la creation de linterface avant laffichage
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createModsManager();
        this.aModsManager.printModList();

        this.aListMods.setHgap(this.aGap);// On defini lecart entre chaque panel
        this.aListMods.setVgap(this.aGap);

        this.aNbRows = this.aModsManager.getNumberOfMods() / this.aNbColumns;

        this.aVersionLabel.setText(this.aVersionString);// Permet d'afficher la version
        this.aNameOfModpackLabel.setText(this.aNameOfModpackString);// Le nom du modpack
        createElements();
    }

    /**
     * Permet de creer ou de mettre a jour la liste visuel des mods
     * 
     * @param nCols
     * @param nRows
     */
    private void createElements() {
        this.aListMods.getChildren().clear();
        int k = 0; // Permet de
        for (int i = 0; i < this.aNbColumns; i++) {
            for (int j = 0; j < this.aNbRows + 1; j++) {
                if (k < this.aModsManager.getNumberOfMods()) {
                    this.aListMods.getChildren().add(createElement(k));
                    k++;
                } else {
                    return;
                }
            }
        }
    }

    /**
     * Permet de cree 1 panel pour l'indice du mods passer en parametre
     * 
     * @param pI
     * @return
     */
    private Pane createElement(int pI) {
        int vWidth = 150;
        int vHeight = 250;
        int vImageScale = 75;
        Pane vPane = new Pane();
        vPane.setPrefSize(vWidth, vHeight);

        Label vName = new Label(this.aModsManager.getMods()[pI].getName());//Nom du mod
        vName.setMaxWidth(vWidth);
        vPane.getChildren().add(vName);

        ImageView vImage = null;
        try {
            vImage = new ImageView(new Image(new FileInputStream(this.aModsManager.getMods()[pI].getIconInJar())));//Son logo
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        vImage.setFitHeight(vImageScale);
        vImage.setFitWidth(vImageScale);
        vImage.setLayoutX((vWidth-vImageScale)/2);
        vImage.setLayoutY((vWidth-vImageScale)/2);
        vPane.getChildren().add(vImage);

        Label vVersion = new Label("Version : " + this.aModsManager.getMods()[pI].getVersion());//sa description
        vVersion.setLayoutY((vImageScale+(vWidth-vImageScale)/2)+2);
        vVersion.setMaxWidth(vWidth);
        vPane.getChildren().add(vVersion);

        Label vDescription = new Label(this.aModsManager.getMods()[pI].getDescription());//sa description
        vDescription.setLayoutY((vImageScale+(vWidth-vImageScale)/2)+16);
        vDescription.setMaxWidth(vWidth);
        vDescription.setMaxHeight(vHeight-(vImageScale+(vWidth-vImageScale)/2)-16);
        vDescription.setWrapText(true);
        vPane.getChildren().add(vDescription);

        NumberFormat vNumberFormat = new DecimalFormat("0.###");//Permet d'arrondir 
        Label vSize = new Label( "" + vNumberFormat.format(this.aModsManager.getMods()[pI].getSize()) + " Mo");//Sa taille en MO
        vSize.setMaxWidth(vWidth);
        vSize.setLayoutY(14);
        vPane.getChildren().add(vSize);
        
        return vPane;
    }

    /**
     * On met a voir l'affichage de la liste de mod
     */
    @FXML
    public void updateList(){
        createModsManager();
        this.aNbRows = this.aModsManager.getNumberOfMods() / this.aNbColumns;
        Platform.runLater(() -> this.aListMods.getChildren().clear());
        Platform.runLater(() -> createElements());
        this.aModsManager.printModList();
    }

    /**
     * Create a mod manager or update it
     */
    public void createModsManager(){
        try {
            this.aModsManager = new ModsManager(this.aFilesManager.listJar(), this.aFilesManager, this.aServerConfig,
                    this);
        } catch (Exception pE) {
            pE.printStackTrace();
        }
    }

    /**
     * Permet de lancer le Thread de mise a jour
     */
    @FXML
    public void updateTaskLaunch(ActionEvent pEvent){
        this.aUpdateTask = new Task<Integer>() {
            @Override
            public Integer call() throws Exception {
                Platform.runLater(() -> aUpdateListButton.setDisable(true));
                Platform.runLater(() -> aUpdateButton.setDisable(true));//On empeche les autres interaction avec le bouton pour lancer que 1 thread de maj
                update();
                Platform.runLater(() -> aUpdateListButton.setDisable(false));
                Platform.runLater(() -> aUpdateButton.setDisable(false));
                return 0;
              }
            };
          Thread th = new Thread(this.aUpdateTask);
          th.setDaemon(true);
          th.start();
    }

    /**
     * Permet de lancer la procedure de mise 
     */
    public void update(){
        if(!(aFilesManager.checkIfModsExists())){//le dossier mods n'existe pas, donc il faut tout telecharger
            this.aFilesManager.createMods();
                try{
                    String[][] vFilesPath = aFilesManager.readXml(Downloader.downloadFile(this.aServerConfig.modpackClient(), "Liste des mods",true,"","",this));//ici on recupere un tableau 1d car 1seul child element sur le xml
                    for(int i = 0; i<vFilesPath.length;i++){
                        Downloader.downloadFile(this.aServerConfig.modpackClient() + vFilesPath[i][0], vFilesPath[i][0],false,"mods/",vFilesPath[i][0],this);
                        updateList();
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
        setLittleUpdateLabel("Mise a jour fini, bon jeu !");
    }	

    /**
     * Permet de changer le text afficher par le label
     * 
     * @param pString
     */
    public void setUpdateLabel(String pString) {
        Platform.runLater(() -> this.aUpdateLabel.setText(pString));// permet de mettre a jours les items depuis nimportquel thread
    }

    public void setLittleUpdateLabel(String pString) {
        Platform.runLater(() -> this.aLittleUpdateLabel.setText(pString));// permet de mettre a jours les items depuis nimportquel thread
    }

    public void setDownloadProgressbar(double pDouble) {
        Platform.runLater(() -> this.aDownloadProgress.setProgress(pDouble));
    }

}
