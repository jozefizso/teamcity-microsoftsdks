package net.izsak.teamcity;

import jetbrains.buildServer.util.Bitness;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import org.apache.log4j.Logger;

import java.util.*;

public class MicrosoftOfficeDetector extends SdkDetector {
    private static final Logger LOG = Logger.getLogger(MicrosoftOfficeDetector.class);

    final static String basePath = "SOFTWARE\\Microsoft\\Office\\";
    final static HashMap<String, String> officeVersions;

    private Win32RegistryAccessor registry;

    static {
        officeVersions = new HashMap<String, String>();
        officeVersions.put("12.0", "2007");
        officeVersions.put("14.0", "2010");
        officeVersions.put("15.0", "2013");
        officeVersions.put("16.0", "2016");
    }

    public MicrosoftOfficeDetector(Win32RegistryAccessor registry){
        this.registry = registry;
    }

    @Override
    public List<SdkVersion> detectSdkVersions(){
        LOG.info("Looking for Microsoft Office applications installed on system.");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Supported Microsoft Office versions by this plugin: "+ combine(officeVersions.keySet().iterator(), ", "));
        }

        List<SdkVersion> detectedVersions = new ArrayList<SdkVersion>();
        for (Map.Entry<String, String> version : officeVersions.entrySet()) {
            this.detectOfficeVersion(detectedVersions, version.getKey(), version.getValue());
        }
        return detectedVersions;
    }

    private void detectOfficeVersion(List<SdkVersion> detectedVersions, String versionNumber, String versionId){
        // sample key: HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Office\16.0\
        // sample key: HKEY_LOCAL_MACHINE\SOFTWARE\WOW6432Node\Microsoft\Office\16.0\
        final String versionPath = basePath + versionNumber;

        LOG.debug("Looking for Microsoft Office version "+ versionNumber +" at registry path HKLM\\"+ versionPath);

        detectOfficeApplicationByInstallRoot(detectedVersions, versionPath, versionNumber, "Common", "Office", versionId);
        detectOfficeApplicationByInstallRoot(detectedVersions, versionPath, versionNumber, "Word", "Word", versionId);
        detectOfficeApplicationByInstallRoot(detectedVersions, versionPath, versionNumber, "Excel", "Excel", versionId);
        detectOfficeApplicationByInstallRoot(detectedVersions, versionPath, versionNumber, "Outlook", "Outlook", versionId);
        detectOfficeApplicationByInstallRoot(detectedVersions, versionPath, versionNumber, "PowerPoint", "PowerPoint", versionId);
    }

    private void detectOfficeApplicationByInstallRoot(List<SdkVersion> detectedVersions, String baseVersionPath, String versionNumber, String officeAppName, String propertyPrefix, String versionId) {
        // sample key: HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Office\16.0\Word\InstallRoot

        final String appPath = baseVersionPath + "\\" + officeAppName;
        final String installRootPath = appPath + "\\InstallRoot";

        LOG.debug("Looking for Microsoft Office application "+ officeAppName +" at registry path HKLM\\"+ installRootPath);

        String installPath = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT32, installRootPath, "Path");
        if (installPath == null) {
            LOG.debug("Did not find InstallRoot\\Path key for Microsoft Office "+ officeAppName +" v"+ versionNumber);
            return;
        }

        SdkVersion sdk = new SdkVersion(propertyPrefix + "_"+ versionId, versionNumber, installPath);
        LOG.info("Found Microsoft Office "+ officeAppName +" v"+ versionNumber +" installed at path '"+ installPath +"'.");
        detectedVersions.add(sdk);
    }
}
