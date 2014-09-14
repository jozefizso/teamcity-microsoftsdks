package net.izsak.teamcity;

public class SdkVersion {
    private String name;
    private String version;
    private String path;

    public SdkVersion(String name, String version, String path) {
        this.name = name;
        this.version = version;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
