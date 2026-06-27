package org.jenkinsci.plugins.prometheus.collectors.jenkins;

import hudson.model.Computer;
import hudson.model.Node;
import hudson.slaves.OfflineCause;
import io.prometheus.client.Collector;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.prometheus.collectors.testutils.MockedJenkinsTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodesOfflineCauseGaugeTest extends MockedJenkinsTest {

    static class UserCause extends OfflineCause {
        @Override
        public String toString() {
            return "user";
        }
    }

    static class DiskSpace extends OfflineCause {
        @Override
        public String toString() {
            return "disk";
        }
    }

    @Test
    public void testCollectResult() throws Exception {

        setFinalStaticTo123(Jenkins.class.getDeclaredField("VERSION"));

        List<Node> nodes = new ArrayList<>();
        nodes.add(offlineNode("node1", new UserCause()));
        nodes.add(onlineNode("node2"));
        nodes.add(offlineNode("node3", new DiskSpace()));
        nodes.add(offlineNode("node4", null));
        nodes.add(offlineNode("node5", new OfflineCause() {
            @Override
            public String toString() {
                return "anon";
            }
        }));
        nodes.add(nullComputerNode());
        when(mock.getNodes()).thenReturn(nodes);

        NodesOfflineCauseGauge sut = new NodesOfflineCauseGauge(new String[]{"node", "cause"}, getNamespace(), getSubSystem());
        sut.calculateMetric(mock, getLabelValues());

        List<Collector.MetricFamilySamples> collect = sut.collect();

        validateMetricFamilySampleListSize(collect, 1);

        Collector.MetricFamilySamples samples = collect.get(0);
        validateNames(samples, new String[]{"default_jenkins_nodes_offline_cause"});
        // 6 nodes in, only the 4 offline ones with a computer produce a series
        validateMetricFamilySampleSize(samples, 4);

        Map<String, String> causeByNode = new HashMap<>();
        for (Collector.MetricFamilySamples.Sample sample : samples.samples) {
            validateValue(sample, 1.0);
            int nodeIdx = sample.labelNames.indexOf("node");
            int causeIdx = sample.labelNames.indexOf("cause");
            causeByNode.put(sample.labelValues.get(nodeIdx), sample.labelValues.get(causeIdx));
        }

        assertEquals("UserCause", causeByNode.get("node1"));
        assertEquals("DiskSpace", causeByNode.get("node3"));
        assertEquals("Unknown", causeByNode.get("node4"));   // null cause
        assertEquals("Unknown", causeByNode.get("node5"));   // anonymous cause has no simple name
        assertFalse(causeByNode.containsKey("node2"));
    }

    private Node offlineNode(String nodeName, OfflineCause cause) {
        Node nodeMock = mock(Node.class);
        Computer computerMock = mock(Computer.class);
        when(computerMock.isOffline()).thenReturn(true);
        when(computerMock.getOfflineCause()).thenReturn(cause);
        when(nodeMock.toComputer()).thenReturn(computerMock);
        when(nodeMock.getNodeName()).thenReturn(nodeName);
        return nodeMock;
    }

    private Node onlineNode(String nodeName) {
        Node nodeMock = mock(Node.class);
        Computer computerMock = mock(Computer.class);
        when(computerMock.isOffline()).thenReturn(false);
        when(nodeMock.toComputer()).thenReturn(computerMock);
        return nodeMock;
    }

    private Node nullComputerNode() {
        Node nodeMock = mock(Node.class);
        when(nodeMock.toComputer()).thenReturn(null);
        return nodeMock;
    }

    static void setFinalStaticTo123(Field field) throws Exception {
        field.setAccessible(true);
        field.set(null, "123");
    }
}
