package fr.lebonq.utils;

public class IdentifierToPath {

    /**
     * All credit to https://github.com/ATLauncher/ATLauncher/blob/5a90f8fcc74cc8bc96dbac91d905f4ce1f5fd1e8/src/main/java/com/atlauncher/utils/Utils.java#L1446
     * @param pIdentifier
     * @return
     */
    public static String convert(String pIdentifier){
        String[] parts = pIdentifier.split(":", 3);
        String name = parts[1];
        String version = parts[2];
        String extension = "jar";
        String classifier = "";
    
        if (version.indexOf('@') != -1) {
            extension = version.substring(version.indexOf('@') + 1);
            version = version.substring(0, version.indexOf('@'));
        }
    
        if (version.indexOf(':') != -1) {
            classifier = "-" + version.substring(version.indexOf(':') + 1);
            version = version.substring(0, version.indexOf(':'));
        }
    
        String path = parts[0].replace(".", "/") + "/" + name + "/" + version + "/" + name + "-" + version + classifier
                + "." + extension;

        return path;
    }
}
