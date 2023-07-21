package org.jenkinsci.plugins.prometheus.config.disabledmetrics;

import org.jenkinsci.plugins.prometheus.config.PrometheusConfiguration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricStatusChecker {
    public static boolean isEnabled(String metricName) {

        PrometheusConfiguration configuration = PrometheusConfiguration.get();
        if (configuration == null) {
            return true;
        }

        DisabledMetricConfig disabledMetricConfig = configuration.getDisabledMetricConfig();
        if (disabledMetricConfig == null) {
            return true;
        }

        List<Entry> entries = disabledMetricConfig.getEntries();
        if (entries == null || entries.isEmpty()) {
            return true;
        }

        for (Entry entry : entries) {
            if (entry instanceof RegexDisabledMetric) {
                Pattern pattern = Pattern.compile(((RegexDisabledMetric) entry).getRegex());
                Matcher matcher = pattern.matcher(metricName);
                return !matcher.matches();
            }

            if (entry instanceof NamedDisabledMetric) {
                return metricName.equalsIgnoreCase(((NamedDisabledMetric) entry).getMetricName());
            }
        }
        return true;
    }
}
