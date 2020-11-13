package fr.lebonq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    private String aAppVersion;

    @Override
    public void start(Stage pStage) throws Exception{
        
        FXMLLoader vLoader = new FXMLLoader();
        vLoader.setController(new AppController(this.aAppVersion));

        InputStream vFXMLFile = getClass().getClassLoader().getResourceAsStream("App.fxml");
        Parent vRoot = vLoader.load(vFXMLFile);

        System.out.print(vRoot);
        Scene vScene = new Scene(vRoot);
        pStage.setTitle("Modpack_Updater");
        pStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("assets/icon.png")));
        pStage.setScene(vScene);
        pStage.show();
    }

    @Override
    public void init() {
        
        final Properties vProperties = new Properties();

        try {
            vProperties.load(getClass().getClassLoader().getResourceAsStream("project.properties")); //Permet d'acceder au fichier ressoruces pour recupérer la version du fichier pom.xml
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.aAppVersion = vProperties.getProperty("version");
        System.out.println("Modpack_updater version " + this.aAppVersion);
    }
    
    public static void main(String[] args) {
        launch(args);       
    }
}