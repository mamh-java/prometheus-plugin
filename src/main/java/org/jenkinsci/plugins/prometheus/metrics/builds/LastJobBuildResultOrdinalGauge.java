package org.jenkinsci.plugins.prometheus.metrics.builds;

import hudson.model.Run;
import io.prometheus.client.Gauge;
import org.jenkinsci.plugins.prometheus.metrics.BaseMetricCollector;

public class LastJobBuildResultOrdinalGauge extends JobBuildResultOrdinalGauge {

    public LastJobBuildResultOrdinalGauge(String[] labelNames, String namespace, String subsystem) {
        super(labelNames, namespace, subsystem);
    }

    @Override
    protected Gauge initCollector() {
        return Gauge.build()
                .name(calculateName("last_build_result_ordinal"))
                .subsystem(subsystem).namespace(namespace)
                .labelNames(labelNames)
                .help("Build status of a job.")
                .create();
    }
}
