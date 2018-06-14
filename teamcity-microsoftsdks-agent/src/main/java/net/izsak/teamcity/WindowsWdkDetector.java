package net.izsak.teamcity;

import jetbrains.buildServer.util.Bitness;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WindowsWdkDetector extends SdkDetector {
    private static final Logger LOG = Logger.getLogger(WindowsWdkDetector.class);

    final String basePath = "SOFTWARE\\Wow6432Node\\Microsoft\\Windows Kits\\Installed Roots";
    final Map<String, String> versionsToDetect = createVersionMap();
    private Win32RegistryAccessor registry;

    public WindowsWdkDetector(Win32RegistryAccessor registry) {
        this.registry = registry;
    }

    private static Map<String, String> createVersionMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("1607", "10.0.14393.0");
        map.put("1703", "10.0.15063.0");
        map.put("1709", "10.0.16299.0");
        map.put("1803", "10.0.17134.0");
        return map;
    }

    @Override
    public List<SdkVersion> detectSdkVersions() {
        LOG.info("Looking for WDKs installed on system.");

        if (LOG.isDebugEnabled()) {
            String versionString = "";
            for (Map.Entry<String, String> version : versionsToDetect.entrySet()) {
                versionString += String.format("(%s,%s) ", version.getKey(), version.getValue());
            }
            LOG.debug("Windows SDK/WDK versions detected by this plugin: " + versionString);
        }

        List<SdkVersion> detectedVersions = new ArrayList<SdkVersion>();
        for (Map.Entry<String, String> version : versionsToDetect.entrySet()) {
            SdkVersion sdkVersion = detectSdkVersion(version);
            if (sdkVersion != null)
                detectedVersions.add(sdkVersion);
        }
        return detectedVersions;
    }

    private SdkVersion detectSdkVersion(Map.Entry<String, String> versionMap) {
        final String version = versionMap.getKey();
        final String buildNumber = versionMap.getValue();
        final String versionKey = String.format("%s\\%s\\Installed Options\\", basePath, buildNumber);

        LOG.debug("Looking for WDK version " + version + " at registry path HKLM\\" + versionKey);

        String fullVersion = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT64, versionKey, "OptionId.WindowsDriverKitComplete");
        if (fullVersion == null) {
            LOG.debug("Did not find FullVersion key for WDK " + version);
            return null;
        }
        LOG.debug("REG value = " + fullVersion);

        String installPath = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT64, basePath, "KitsRoot10");
        if (installPath == null) {
            LOG.debug("Did not find InstallPath key for WDK " + version);
            return null;
        }
        installPath += String.format("bin\\%s", buildNumber);

        SdkVersion sdk = new SdkVersion("WDK10_" + version, buildNumber, installPath);
        LOG.info("Found WDK version " + version + " installed at path '" + installPath + "'.");
        return sdk;
    }
}
