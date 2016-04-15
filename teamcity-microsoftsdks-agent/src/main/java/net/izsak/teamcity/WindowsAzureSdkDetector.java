package net.izsak.teamcity;

import jetbrains.buildServer.util.Bitness;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WindowsAzureSdkDetector extends SdkDetector {
    private static final Logger LOG = Logger.getLogger(WindowsAzureSdkDetector.class);

    final String basePath = "SOFTWARE\\Microsoft\\Microsoft SDKs\\ServiceHosting\\";
    final String[] versionsToDetect = new String[] {
            "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8",
            "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9"
    };

    private Win32RegistryAccessor registry;

    public WindowsAzureSdkDetector(Win32RegistryAccessor registry){
        this.registry = registry;
    }

    @Override
    public List<SdkVersion> detectSdkVersions(){
        LOG.info("Looking for Azure SDKs installed on system.");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Supported Azure SDK versions by this plugin: "+ combine(versionsToDetect, ", "));
        }

        List<SdkVersion> detectedVersions = new ArrayList<SdkVersion>();
        for (String version : versionsToDetect) {
            SdkVersion sdkVersion = detectSdkVersion(version);
            if (sdkVersion != null)
                detectedVersions.add(sdkVersion);
        }
        return detectedVersions;
    }

    private SdkVersion detectSdkVersion(String version){
        // sample key: HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SDKs\ServiceHosting\v1.7
        final String versionPath = basePath + "v" + version;

        LOG.debug("Looking for Azure SDK version "+ version +" at registry path HKLM\\"+ versionPath);

        String fullVersion = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT32, versionPath, "FullVersion");
        if (fullVersion == null) {
            LOG.debug("Did not find FullVersion key for Azure SDK "+ version);
            return null;
        }

        String installPath = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT32, versionPath, "InstallPath");
        if (installPath == null) {
            LOG.debug("Did not find InstallPath key for Azure SDK "+ version);
            return null;
        }

        SdkVersion sdk = new SdkVersion("AzureSDK_"+ version, fullVersion, installPath);
        LOG.info("Found Azure SDK version "+ version +" installed at path '"+ installPath +"'.");
        return sdk;
    }
}
