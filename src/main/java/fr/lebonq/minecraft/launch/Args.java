package fr.lebonq.minecraft.launch;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import fr.lebonq.minecraft.libraries.Librarie;
/**
 * Ne pas oublier lespace apres les strings
 */
public class Args {
    private static final String ARG1 = "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump ";
    private String aArg2;//Nom OS
    private String aArg3;//Version OS
    private static final String ARG4 = "-Xss1M ";
    private String aArg5;//Chemin des natives extraites
    private String aArg6;//Nom launcher
    private String aArg7;//Launcher Version
    private String aArg8; //Classpath
    private static final String ARG9 = "-Xmx5G ";//Ram max 5g
    private static final String ARG10 = "-XX:+UnlockExperimentalVMOptions ";
    private static final String ARG11 = "-XX:+UseG1GC ";
    private static final String ARG12 = "-XX:G1NewSizePercent=20 ";
    private static final String ARG13 = "-XX:G1ReservePercent=20 ";
    private static final String ARG14 = "-XX:MaxGCPauseMillis=50 ";
    private static final String ARG15 = "-XX:G1HeapRegionSize=32M ";
    private String aArg16;//Path to Dlog4j
    private String aArg17;//username
    private String aArg18;//version
    private String aArg19;//game dir ex : C:\Users\lebon\AppData\Roaming\.minecraft 
    private String aArg20;//asset dir
    private String aArg21;//assest index ex 1.16 
    private String aArg22;//UUID
    private String aArg23;//ACCES TOKEN
    private static final String ARG24 = "--userType mojang ";
    private static final String ARG25 = "--versionType release ";

    /**
     * Nom os
     */
    public void setArg2(){
        this.aArg2 = "\"-Dos.name=Windows 10\" " ;
    }

    /**
     * Verison OS
     */
    public void setArg3(){
        this.aArg3 = "-Dos.version=10.0 ";
    }

    /**
     * Chemin vers bin extrait
     */
    public void setArg5(String pPath){
        this.aArg5 = "-Djava.library.path=" + pPath + " ";
    }

    /**
     * Nom du laucnher
     * @param pName
     */
    public void setArg6(String pName){
        this.aArg6 = "-Dminecraft.launcher.brand=" + pName +" ";
    }

    /**
     * Version laucnher
     * @param pVersion
     */
    public void setArg7(String pVersion){
        this.aArg7 = "-Dminecraft.launcher.version=" + pVersion +" ";
    }

    /**
     * Class path and client
     * @param pLibraries
     * @param pClientJar
     */
    public void setArg8(List<Librarie> pLibraries,File pClientJar){
        Iterator<Librarie> vIt = pLibraries.iterator();
        String vReturString = "";
        while (vIt.hasNext()) {
            Librarie vTemp = vIt.next();
            vReturString += vTemp.getFile() + ";";
        }
        vReturString += pClientJar.getAbsolutePath();
        this.aArg8 = "-cp " + vReturString +" ";
    }

    /**
     * Pasth to dlog4j
     * @param pFile
     */
    public void setArg16(File pFile){
        this.aArg16 = "-Dlog4j.configurationFile=" + pFile.getAbsolutePath() +" net.fabricmc.loader.launch.knot.KnotClient ";
    }

    /**
     * Username
     * @param aArg17
     */
    public void setArg17(String aArg17) {
        this.aArg17 = "--username "+ aArg17+" ";
    }

    /**
     * Version string
     * @param aArg18
     */
    public void setArg18(String aArg18) {
        this.aArg18 = "--version " +aArg18+" ";
    }

    /**
     * Game dir
     */
    public void setArg19(File aArg19) {
        this.aArg19 = "--gameDir " + aArg19.getAbsolutePath()+" ";
    }

    /**
     * Assest dir
     * @param aArg20
     */
    public void setArg20(File aArg20) {
        this.aArg20 = "--assetsDir " + aArg20.getAbsolutePath()+" ";
    }

    /**
     * Index file without json extention
     * @param aArg21
     */
    public void setArg21(String aArg21) {
        this.aArg21 = "--assetIndex " + aArg21+" ";
    }

    /**
     * uuid setter
     * @param aArg22
     */
    public void setArg22(String aArg22) {
        this.aArg22 = "--uuid " + aArg22+" ";
    }

    /**
     * Acces token setter
     * @param aArg23
     */
    public void setArg23(String aArg23) {
        this.aArg23 = "--accessToken " + aArg23+" ";
    }

    public String getArg1() {
        return ARG1;
    }

    public String getArg2() {
        return this.aArg2;
    }

    public String getArg3() {
        return this.aArg3;
    }

    public String getArg4() {
        return ARG4;
    }

    public String getArg5() {
        return this.aArg5;
    }

    public String getArg6() {
        return this.aArg6;
    }

    public String getArg7() {
        return this.aArg7;
    }

    public String getArg8() {
        return this.aArg8;
    }

    public String getArg9() {
        return ARG9;
    }

    public String getArg10() {
        return ARG10;
    }

    public String getArg11() {
        return ARG11;
    }

    public String getArg12() {
        return ARG12;
    }

    public String getArg13() {
        return ARG13;
    }

    public String getArg14() {
        return ARG14;
    }

    public String getArg15() {
        return ARG15;
    }

    public String getArg16() {
        return this.aArg16;
    }

    public String getArg17() {
        return this.aArg17;
    }

    public String getArg18() {
        return this.aArg18;
    }

    public String getArg19() {
        return this.aArg19;
    }

    public String getArg20() {
        return this.aArg20;
    }

    public String getArg21() {
        return this.aArg21;
    }

    public String getArg22() {
        return this.aArg22;
    }

    public String getArg23() {
        return this.aArg23;
    }

    public String getArg24() {
        return ARG24;
    }

    public String getArg25() {
        return ARG25;
    }
    
    public String getCommand(){
    
        return  "java " +ARG1
        +this.aArg2
        +this.aArg3
        +ARG4
        +this.aArg5
        +this.aArg6
        +this.aArg7
        +this.aArg8
        +ARG9
        +ARG10
        +ARG11
        +ARG12
        +ARG13
        +ARG14
        +ARG15
        +this.aArg16
        +this.aArg17
        +this.aArg18
        +this.aArg19
        +this.aArg20
        +this.aArg21
        +this.aArg22
        +this.aArg23
        +ARG24
        +ARG25;

    }
}