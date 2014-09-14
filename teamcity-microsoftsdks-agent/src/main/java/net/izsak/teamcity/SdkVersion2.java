package net.izsak.teamcity;

public class SdkVersion2 {
    private String name;
    private String version;
    private String registryKey;

    public SdkVersion2(String name, String version, String registryKey) {
        this.name = name;
        this.version = version;
        this.registryKey = registryKey;
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

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }
}
