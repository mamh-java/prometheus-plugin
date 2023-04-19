package org.jenkinsci.plugins.prometheus.metrics.builds;

import hudson.model.Result;
import io.prometheus.client.Collector;
import org.jenkinsci.plugins.prometheus.metrics.testutils.MockedRunCollectorTest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.util.List;

public class JobBuildResultOrdinalGaugeTest extends MockedRunCollectorTest {


    @Test
    public void testNothingCalculatedAsRunNotYetOver() {

        Mockito.when(mock.getResult()).thenReturn(null);

        JobBuildResultOrdinalGauge sut = new JobBuildResultOrdinalGauge(getLabelNames(), getNamespace(), getSubSystem());

        sut.initCollector();

        sut.calculateMetric(mock, getLabelValues());

        List<Collector.MetricFamilySamples> collect = sut.collect();

        Assertions.assertEquals(1, collect.size());
        Assertions.assertEquals(0, collect.get(0).samples.size(), "Would expect no result");
    }

    @Test
    public void testOrdinalCalculated() {

        Mockito.when(mock.getResult()).thenReturn(Result.SUCCESS);

        JobBuildResultOrdinalGauge sut = new JobBuildResultOrdinalGauge(getLabelNames(), getNamespace(), getSubSystem());

        sut.initCollector();

        sut.calculateMetric(mock, getLabelValues());

        List<Collector.MetricFamilySamples> collect = sut.collect();

        Assertions.assertEquals(1, collect.size());
        Assertions.assertEquals(1, collect.get(0).samples.size(), "Would expect one result");


        Assertions.assertEquals("default_jenkins_builds_build_result_ordinal", collect.get(0).samples.get(0).name);
        Assertions.assertEquals(0.0, collect.get(0).samples.get(0).value);

    }
}