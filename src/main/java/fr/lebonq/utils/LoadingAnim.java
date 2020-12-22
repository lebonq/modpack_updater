package fr.lebonq.utils;

public class LoadingAnim {

    private LoadingAnim(){
        throw new IllegalStateException("Utility class");
    }

    public static String anim(long  pPercent){
        long vMaxItem = 20;
        long vNbLoaditem = (pPercent*vMaxItem)/100l;
        StringBuilder vBuilder = new StringBuilder();
        vBuilder.append("[");
        long j = 0;
        for(long i = 0; i < vMaxItem;i++){
            if(j<=vNbLoaditem){
                vBuilder.append("#");
                j++;
            }
            else{
                vBuilder.append(" ");
            }
        }
        vBuilder.append("] " + pPercent + " %");
        return vBuilder.toString();
    }
    
}
