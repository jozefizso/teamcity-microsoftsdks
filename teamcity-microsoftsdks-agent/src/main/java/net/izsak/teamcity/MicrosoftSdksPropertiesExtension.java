package net.izsak.teamcity;

import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.BuildAgent;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.util.Win32RegistryAccessor;
import jetbrains.buildServer.util.positioning.PositionAware;
import jetbrains.buildServer.util.positioning.PositionConstraint;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class MicrosoftSdksPropertiesExtension extends AgentLifeCycleAdapter implements PositionAware {
    private static final Logger LOG = Logger.getLogger(MicrosoftSdksPropertiesExtension.class);

    private final Win32RegistryAccessor registry;

    public MicrosoftSdksPropertiesExtension(final EventDispatcher<AgentLifeCycleListener> events, final Win32RegistryAccessor registry) {
        this.registry = registry;
        events.addListener(this);
    }

    public String getOrderId() {
        return "MicrosoftSdks";
    }

    public PositionConstraint getConstraint() {
        return PositionConstraint.last();
    }

    @Override
    public void beforeAgentConfigurationLoaded(final BuildAgent agent) {
        final BuildAgentConfiguration config = agent.getConfiguration();
        if (!config.getSystemInfo().isWindows()) {
            return;
        }

        if (registry == null) {
            LOG.error("Object Win32RegistryAccessor was not provided by Spring Framework on Windows Platform. Cannot read data from registry.");
            return;
        }

        List<SdkDetector> detectors = new ArrayList<SdkDetector>();
        detectors.add(new WindowsAzureSdkDetector(this.registry));
        detectors.add(new WindowsPhoneSdkDetector(this.registry));
        detectors.add(new AspnetMvcDetector(this.registry));
        detectors.add(new FSharpDetector(this.registry));
        detectors.add(new MicrosoftOfficeDetector(this.registry));
        detectors.add(new WindowsWdkDetector(this.registry));

        for (SdkDetector detector : detectors) {
            List<SdkVersion> detectedVersions = detector.detectSdkVersions();
            for (SdkVersion version : detectedVersions) {
                config.addConfigurationParameter(version.getName(), version.getFullVersion());
                config.addConfigurationParameter(version.getName() + "_Path", version.getPath());
            }
        }
    }
}
