package fr.lebonq.minecraft.versions_manifest;

public class Latest {
    private String release;
    private String snapshot;

    public Latest() {
        //Vide car utiliser par GSON
    }

    public String getRelease() {
        return this.release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getSnapshot() {
        return this.snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

}
