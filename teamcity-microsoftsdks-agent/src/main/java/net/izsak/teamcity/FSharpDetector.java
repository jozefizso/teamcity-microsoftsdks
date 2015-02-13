package net.izsak.teamcity;

import jetbrains.buildServer.util.Bitness;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FSharpDetector extends SdkDetector {
    private static final Logger LOG = Logger.getLogger(FSharpDetector.class);

    final static List<SdkVersion2> versions;

    static {
        versions = new ArrayList<SdkVersion2>();
        versions.add(new SdkVersion2("FSHARP_TOOLS_3_1_2", "Visual F# Tools 3.1.2", "SOFTWARE\\Classes\\Installer\\Dependencies\\Microsoft.VS.FSharpSDK,v12"));
    }

    private Win32RegistryAccessor registry;

    public FSharpDetector(Win32RegistryAccessor registry){
        this.registry = registry;
    }

    @Override
    public List<SdkVersion> detectSdkVersions(){
        LOG.info("Looking for Visual F# Tools installed on system.");

        if (LOG.isDebugEnabled()) {
            String[] names = new String[versions.size()];
            for (int i = 0; i < versions.size(); i++)
                names[i] = versions.get(i).getFullVersion();

            LOG.debug("Supported Visual F# Tools versions by this plugin: "+ combine(names, ", "));
        }

        List<SdkVersion> detectedVersions = new ArrayList<SdkVersion>();
        for (SdkVersion2 version : versions) {
            SdkVersion sdkVersion = detectSdkVersion(version);
            if (sdkVersion != null)
                detectedVersions.add(sdkVersion);
        }
        return detectedVersions;
    }

    private SdkVersion detectSdkVersion(SdkVersion2 version){
        LOG.debug("Looking for "+ version.getFullVersion() +" at registry path HKLM\\"+ version.getRegistryKey());

        String displayName = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT32, version.getRegistryKey(), "DisplayName");
        if (displayName == null) {
            LOG.debug("Did not find DisplayName key for "+ version.getName());
            return null;
        }

        SdkVersion sdk = new SdkVersion(version.getName(), version.getFullVersion(), displayName);
        LOG.info("Found Visual F# Tools version "+ version.getFullVersion() +" installed.");
        return sdk;
    }

}