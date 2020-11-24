package fr.lebonq.minecraft.versions_manifest;

/**
 * Permet de lire et de stocker les version du fichier . json
 */
public class Version {
    private String id;
    private String type;
    private String url;
    private String time;
    private String releaseTime;


    public Version() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReleaseTime() {
        return this.releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", type='" + getType() + "'" +
            ", url='" + getUrl() + "'" +
            ", time='" + getTime() + "'" +
            ", releaseTime='" + getReleaseTime() + "'" +
            "}";
    }
}
