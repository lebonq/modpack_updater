package fr.lebonq.utils;

public class LoadingAnim {

    public static String anim(long  pPercent){
        long vMaxItem = 20;
        long vNbLoaditem = (pPercent*vMaxItem)/100l;
        String vToPrint = "[";
        long j = 0;
        for(long i = 0; i < vMaxItem;i++){
            if(j<=vNbLoaditem){
                vToPrint += "#";
                j++;
            }
            else{
                vToPrint  += " ";
            }
        }
        vToPrint += "] " + pPercent + " %";
        return vToPrint;
    }
    
}
