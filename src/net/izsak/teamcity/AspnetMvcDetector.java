package net.izsak.teamcity;

import jetbrains.buildServer.util.Bitness;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AspnetMvcDetector extends SdkDetector {
    private static final Logger LOG = Logger.getLogger(AspnetMvcDetector.class);

    final static List<SdkVersion2> versions;

    static {
        versions = new ArrayList<SdkVersion2>();
        versions.add(new SdkVersion2("ASPNET_MVC_1", "ASP.NET MVC 1.0", "SOFTWARE\\Wow6432Node\\Microsoft\\ASP.NET\\ASP.NET MVC 1.0"));
        versions.add(new SdkVersion2("ASPNET_MVC_2", "ASP.NET MVC 2", "SOFTWARE\\Wow6432Node\\Microsoft\\ASP.NET MVC 2\\Runtime"));
        versions.add(new SdkVersion2("ASPNET_MVC_3", "ASP.NET MVC 3", "SOFTWARE\\Wow6432Node\\Microsoft\\ASP.NET MVC 3\\Runtime"));
        versions.add(new SdkVersion2("ASPNET_MVC_4", "ASP.NET MVC 4", "SOFTWARE\\Wow6432Node\\Microsoft\\ASP.NET MVC 4\\Runtime"));
    }

    private Win32RegistryAccessor registry;

    public AspnetMvcDetector(Win32RegistryAccessor registry){
        this.registry = registry;
    }

    @Override
    public List<SdkVersion> detectSdkVersions(){
        LOG.info("Looking for ASP.NET MVC installated on system.");

        if (LOG.isDebugEnabled()) {
            String[] names = new String[versions.size()];
            for (int i = 0; i < versions.size(); i++)
                names[i] = versions.get(i).getFullVersion();

            LOG.debug("Supported ASP.NET MVC versions: "+ combine(names, ", "));
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

        String installPath = this.registry.readRegistryText(Win32RegistryAccessor.Hive.LOCAL_MACHINE, Bitness.BIT32, version.getRegistryKey(), "InstallPath");
        if (installPath == null) {
            LOG.debug("Did not find InstallPath key for "+ version.getName());
            return null;
        }

        SdkVersion sdk = new SdkVersion(version.getName(), version.getFullVersion(), installPath);
        LOG.info("Found ASP.NET MVC version "+ version.getFullVersion() +" installed at path '"+ installPath +"'.");
        return sdk;
    }

}