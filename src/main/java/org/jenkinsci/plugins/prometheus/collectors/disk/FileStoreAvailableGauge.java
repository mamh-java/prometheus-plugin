package org.jenkinsci.plugins.prometheus.collectors.disk;

import io.prometheus.client.Gauge;
import io.prometheus.client.SimpleCollector;
import org.jenkinsci.plugins.prometheus.collectors.BaseMetricCollector;
import org.jenkinsci.plugins.prometheus.collectors.CollectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileStore;

public class FileStoreAvailableGauge extends BaseMetricCollector<FileStore, Gauge> {


    private static final Logger LOGGER = LoggerFactory.getLogger(FileStoreAvailableGauge.class);

    protected FileStoreAvailableGauge(String[] labelNames, String namespace, String subsystem) {
        super(labelNames, namespace, subsystem);
    }

    @Override
    protected CollectorType getCollectorType() {
        return CollectorType.FILE_STORE_AVAILABLE_GAUGE;
    }

    @Override
    protected String getHelpText() {
        return "Estimated available space on the file stores used by Jenkins";
    }

    @Override
    protected SimpleCollector.Builder<?, Gauge> getCollectorBuilder() {
        return Gauge.build();
    }

    @Override
    public void calculateMetric(FileStore jenkinsObject, String[] labelValues) {
        if (jenkinsObject == null) {
            return;
        }
        try {
            this.collector.labels(labelValues).set(jenkinsObject.getUsableSpace());
        } catch (IOException | RuntimeException e) {
            LOGGER.debug("Failed to get usable space of {}", jenkinsObject, e);
            this.collector.labels(labelValues).set(Double.NaN);
        }
    }
}
