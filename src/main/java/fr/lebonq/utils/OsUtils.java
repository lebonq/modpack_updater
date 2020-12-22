package fr.lebonq.utils;

public final class OsUtils {

    private OsUtils(){
        throw new IllegalStateException("Utility class");
    }

    private static String getOsName(){
        return System.getProperty("os.name").toLowerCase();
    }

    public static boolean isWindows(){
        return getOsName().startsWith("windows");
    }

    public static boolean isMac(){
        return getOsName().startsWith("mac");
    }

    public static boolean isUnix(){
        return getOsName().startsWith("nux");
    }

    public static String getOsArchitecture(){
        return (System.getProperty("os.arch"));
    }
}
