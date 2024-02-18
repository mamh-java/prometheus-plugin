package org.jenkinsci.plugins.prometheus.config.disabledmetrics;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class RegexDisabledMetric extends Entry {

    private final String regex;

    @DataBoundConstructor
    public RegexDisabledMetric(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    @Override
    public Descriptor<Entry> getDescriptor() {
        return new DescriptorImpl();
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Entry> {
        @Override
        @NonNull
        public String getDisplayName() {
            return "Regex Entry";
        }

    }
}
