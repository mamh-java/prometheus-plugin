package org.jenkinsci.plugins.prometheus.collectors.builds;

import io.prometheus.client.Collector;
import jenkins.metrics.impl.TimeInQueueAction;
import org.jenkinsci.plugins.prometheus.collectors.testutils.MockedRunCollectorTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;


public class BuildWaitingDurationGaugeTest extends MockedRunCollectorTest {

    @Test
    public void testCalculateDurationWhenRunIsNotBuilding() {
        Mockito.when(mock.isBuilding()).thenReturn(false);
        TimeInQueueAction timeInQueueAction = new TimeInQueueAction(200,1,1,1);
        Mockito.doReturn(timeInQueueAction).when(mock).getAction(TimeInQueueAction.class);

        BuildWaitingDurationGauge sut = new BuildWaitingDurationGauge(getLabelNames(), getNamespace(), getSubSystem(), "");

        sut.calculateMetric(mock, getLabelValues());

        List<Collector.MetricFamilySamples> collect = sut.collect();
        Assertions.assertEquals(1, collect.size());
        Assertions.assertEquals(200L, collect.get(0).samples.get(0).value);
        Assertions.assertEquals("default_jenkins_builds_build_waiting_milliseconds", collect.get(0).samples.get(0).name);
    }

    @Test
    public void testCalculateDurationIsNotCalculatedWhenRunIsBuilding() {
        Mockito.when(mock.isBuilding()).thenReturn(true);

        BuildWaitingDurationGauge sut = new BuildWaitingDurationGauge(getLabelNames(), getNamespace(), getSubSystem(), "");

        sut.calculateMetric(mock, getLabelValues());

        List<Collector.MetricFamilySamples> collect = sut.collect();
        Assertions.assertEquals(1, collect.size());
        Assertions.assertEquals(0, collect.get(0).samples.size());
    }
}