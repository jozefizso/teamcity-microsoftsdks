package net.izsak.teamcity;

import jetbrains.buildServer.util.Bitness;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WindowsPhoneSdkDetector extends SdkDetector {
    private static final Logger LOG = Logger.getLogger(WindowsPhoneSdkDetector.class);

    final String basePath = "SOFTWARE\\Microsoft\\Microsoft SDKs\\WindowsPhone\\";
    final String[] versionsToDetect = new String[] { "7.0", "7.1" };

    private Win32RegistryAccessor registry;

    public WindowsPhoneSdkDetector(Win32RegistryAccessor registry){
        this.registry = registry;
    }

    @Override
    public List<SdkVersion> detectSdkVersions(){
        LOG.info("Looking for Windows Phone SDKs installated on system.");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Supported Windows Phone SDK versions: "+ combine(versionsToDetect, ", "));
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
        // sample key: HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SDKs\WindowsPhone\v7.0
        // sample key: HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Microsoft SDKs\WindowsPhone\v7.0\Install Path
        final String versionPath = basePath + "v" + version;
        final String installPathKey = versionPath + "\\Install Path";

        LOG.debug("Looking for Windows Phone SDK version "+ version +" at registry path HKLM\\"+ versionPath);

        String fullVersion = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT32, versionPath, "Version");
        if (fullVersion == null) {
            LOG.debug("Did not find Version key for Windows Phone SDK "+ version);
            return null;
        }

        String installPathValue = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT32, installPathKey, "Install Path");
        if (installPathValue == null) {
            LOG.debug("Did not find 'Install Path' key for Windows Phone SDK "+ version);
            return null;
        }

        SdkVersion sdk = new SdkVersion("WindowsPhoneSDK_"+ version, fullVersion, installPathValue);
        LOG.info("Found Windows Phone SDK version "+ version +" installed at path '"+ installPathValue +"'.");
        return sdk;
    }
}