package fr.lebonq.mods;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;


public class Mod {
    private File aFile;
    private String aName;
    private String aVersion;
    private File aIconInJar;
    private String aDescription;
    private double aSize;

    public Mod(final File pFile, final String pName, final String pVersion, final File pIcon, final String pDescription) {
        this.aFile = pFile;
        this.aName = pName;
        this.aVersion = pVersion;
        this.aIconInJar = pIcon;
        this.aDescription = pDescription;
        try {
            this.aSize = ((Files.size(Paths.get(this.aFile.getAbsolutePath())) / 1024d) / 1024d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        DecimalFormat pDf = new DecimalFormat("#.####"); //Permet d'arrondir a 4 chiffres apres la virgule
        pDf.setRoundingMode(RoundingMode.HALF_UP);//Arrondir par exces
        return this.aName + " version " + this.aVersion + " | " + pDf.format(this.aSize) + " Mo";
    }

    public File getFile() {
        return this.aFile;
    }

    public String getName() {
        return this.aName;
    }

    public String getVersion() {
        return this.aVersion;
    }

    public String getDescription() {
        return this.aDescription;
    }

    public File getIconInJar() {
        return this.aIconInJar;
    }

    public double getSize() {
        return this.aSize;
    }

}
