package dev.techh.configuration.data;

import java.util.List;
import java.util.Map;

public class Configuration {
    private Map<String, List<Rule>> rules;

    public Map<String, List<Rule>> getRules() {
        return rules;
    }

    public void setRules(Map<String, List<Rule>> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        rules.forEach((className, rules) -> {
            sb.append(className).append(" rules=").append(rules.size());
        });

        return sb.toString();
    }
}
