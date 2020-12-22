package fr.lebonq.utils;

public class IsContain {

    private IsContain(){
        throw new IllegalStateException("Utility class");
    }

    public static boolean isContain(int pI, int pJ,String[][] pTab,String pUnkonw){
        for(int i = pI; i < pTab.length; i++){
            if(pTab[i][pJ].equals(pUnkonw)){
                return true;
            }
        }
        return false;
    }
}
