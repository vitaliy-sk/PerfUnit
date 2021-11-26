package dev.techh.configuration.data;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Configuration {

    private long storageLimit = 100_000_000_000L;
    private Map<String, Rule> rules;

    public long getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(long storageLimit) {
        this.storageLimit = storageLimit;
    }

    public Map<String, Rule> getRules() {
        return rules;
    }

    public void setRules(Map<String, Rule> rules) {
        this.rules = rules;
    }

    public Set<String> getClasses() {
        return rules.keySet().stream()
                .map(ref -> ref.split("#")[0])
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        rules.forEach((key, rule) -> sb.append(key));
        return sb.toString();
    }
}
