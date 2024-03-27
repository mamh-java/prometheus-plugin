package org.jenkinsci.plugins.prometheus.init;

import hudson.ExtensionList;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import org.jenkinsci.plugins.prometheus.service.PrometheusMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Initializers {

    private static final Logger LOGGER = LoggerFactory.getLogger(Initializers.class.getName());

    @Initializer(before = InitMilestone.COMPLETED)
    public static void initializePrometheusMetrics() {
        LOGGER.debug("Initializing Prometheus Plugin");
        try {
            PrometheusMetrics prometheusMetrics = ExtensionList.lookupSingleton(PrometheusMetrics.class);
            prometheusMetrics.initialize();
        } catch (IllegalStateException e) {
            LOGGER.error("Unable to load Prometheus Plugin. Collecting Metrics won't work", e);
        }
    }

}
