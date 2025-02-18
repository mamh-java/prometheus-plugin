package org.jenkinsci.plugins.prometheus.collectors.builds;

import hudson.model.Run;
import io.prometheus.client.Gauge;
import io.prometheus.client.SimpleCollector;
import jenkins.metrics.impl.TimeInQueueAction;
import org.jenkinsci.plugins.prometheus.collectors.CollectorType;

public class BuildWaitingDurationGauge extends BuildsMetricCollector<Run<?, ?>, Gauge> {

    protected BuildWaitingDurationGauge(String[] labelNames, String namespace, String subsystem, String namePrefix) {
        super(labelNames, namespace, subsystem, namePrefix);
    }

    @Override
    protected CollectorType getCollectorType() {
        return CollectorType.BUILD_WAITING_GAUGE;
    }

    @Override
    protected String getHelpText() {
        return "Duration this Run spent queuing, that is the wall time from when it entered the queue until it left the queue.";
    }

    @Override
    protected SimpleCollector.Builder<?, Gauge> getCollectorBuilder() {
        return Gauge.build();
    }

    @Override
    public void calculateMetric(Run<?, ?> jenkinsObject, String[] labelValues) {
        if (!jenkinsObject.isBuilding()) {
            TimeInQueueAction action = jenkinsObject.getAction(TimeInQueueAction.class);
            if (action != null) {
                long queuingDurationMillis = action.getQueuingDurationMillis();
                collector.labels(labelValues).set(queuingDurationMillis);
            }
        }
    }
}
