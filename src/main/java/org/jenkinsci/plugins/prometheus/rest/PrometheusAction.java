package org.jenkinsci.plugins.prometheus.rest;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import hudson.util.HttpResponses;
import io.prometheus.client.exporter.common.TextFormat;
import jenkins.metrics.api.Metrics;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.prometheus.config.PrometheusConfiguration;
import org.jenkinsci.plugins.prometheus.service.DefaultPrometheusMetrics;
import org.jenkinsci.plugins.prometheus.service.PrometheusMetrics;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Extension
public class PrometheusAction implements UnprotectedRootAction {

    private final PrometheusMetrics prometheusMetrics = DefaultPrometheusMetrics.get();

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Prometheus Metrics Exporter";
    }

    @Override
    public String getUrlName() {
        return PrometheusConfiguration.get().getUrlName();
    }

    public HttpResponse doDynamic(StaplerRequest2 request) {
        if (request.getRestOfPath().equals(PrometheusConfiguration.get().getAdditionalPath())) {
            if (hasAccess()) {
                return prometheusResponse();
            }
            return HttpResponses.forbidden();
        }
        return HttpResponses.notFound();
    }

    private boolean hasAccess() {
        if (PrometheusConfiguration.get().isUseAuthenticatedEndpoint()) {
            return Jenkins.get().hasPermission(Metrics.VIEW);
        }
        return true;
    }


    private HttpResponse prometheusResponse() {
        return new HttpResponse() {
            @Override
            public void generateResponse(StaplerRequest2 request, StaplerResponse2 response, Object node) throws IOException {
                response.setStatus(StaplerResponse2.SC_OK);
                response.setContentType(TextFormat.CONTENT_TYPE_004);
                response.addHeader("Cache-Control", "must-revalidate,no-cache,no-store");
                response.getWriter().write(prometheusMetrics.getMetrics());
            }
        };
    }
}
