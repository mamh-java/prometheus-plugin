package org.jenkinsci.plugins.prometheus.collectors.jenkins;

import hudson.model.Computer;
import hudson.model.Node;
import hudson.slaves.OfflineCause;
import io.prometheus.client.Gauge;
import io.prometheus.client.SimpleCollector;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.prometheus.collectors.BaseMetricCollector;
import org.jenkinsci.plugins.prometheus.collectors.CollectorType;

public class NodesOfflineCauseGauge extends BaseMetricCollector<Jenkins, Gauge> {

    NodesOfflineCauseGauge(String[] labelNames, String namespace, String subsystem) {
        super(labelNames, namespace, subsystem);
    }

    @Override
    protected CollectorType getCollectorType() {
        return CollectorType.NODES_OFFLINE_CAUSE_GAUGE;
    }

    @Override
    protected String getHelpText() {
        return "Offline cause per offline node";
    }

    @Override
    protected SimpleCollector.Builder<?, Gauge> getCollectorBuilder() {
        return Gauge.build();
    }

    @Override
    public void calculateMetric(Jenkins jenkinsObject, String[] labelValues) {
        if (jenkinsObject == null) {
            return;
        }
        for (Node node : jenkinsObject.getNodes()) {
            // Only offline nodes get a series, labelled with their offline cause
            Computer comp = node.toComputer();
            if (comp == null || !comp.isOffline()) {
                continue;
            }
            this.collector.labels(node.getNodeName(), causeType(comp.getOfflineCause())).set(1);
        }
    }

    private static String causeType(OfflineCause cause) {
        if (cause == null) {
            return "Unknown";
        }
        String name = cause.getClass().getSimpleName();
        return name.isEmpty() ? "Unknown" : name;
    }
}
