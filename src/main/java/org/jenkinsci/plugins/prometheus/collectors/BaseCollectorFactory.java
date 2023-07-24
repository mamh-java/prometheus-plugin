package org.jenkinsci.plugins.prometheus.collectors;

import org.jenkinsci.plugins.prometheus.config.disabledmetrics.MetricStatusChecker;
import org.jenkinsci.plugins.prometheus.util.ConfigurationUtils;

public abstract class BaseCollectorFactory {

    protected final String namespace;
    protected final String subsystem;

    public BaseCollectorFactory() {
        namespace = ConfigurationUtils.getNamespace();
        subsystem = ConfigurationUtils.getSubSystem();
    }

    protected boolean isEnabledViaConfig(CollectorType type) {
        String fullName = namespace + "_" + subsystem + "_" + type.getName();
        return MetricStatusChecker.isEnabled(fullName);
    }
}
