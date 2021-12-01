package dev.techh.perfunit.reporter;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import dev.techh.perfunit.configuration.data.Rule;
import dev.techh.perfunit.exception.LimitReachedException;
import dev.techh.perfunit.utils.StackTraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileMarkdownReporter implements Reporter, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Mustache reportTemplate;
    private final Mustache summaryTemplate;
    private final File summaryOutputFolder;
    private final File rulesOutputFolder;

    // TODO Move violations to PerfUnitStorage

    // rule id, violation count
    private Map<Rule, Integer> violations = new HashMap<>();

    // trace id, violated rules ids
    private Map<Integer, Set<String>> traces = new HashMap<>();

    // TODO Move to config
    private boolean ignoreTraceDuplicates = true;
    private String folder = "./perfunit-report/";

    public FileMarkdownReporter() {
        Runtime.getRuntime().addShutdownHook(new Thread(this) );

        summaryOutputFolder = new File(folder);
        rulesOutputFolder = new File(summaryOutputFolder, "rules");

        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        reportTemplate = mustacheFactory.compile("template/markdown/rule.mustache");
        summaryTemplate = mustacheFactory.compile("template/markdown/summary.mustache");
        cleanReportFolder();
    }

    private void cleanReportFolder() {
        try {
            if (summaryOutputFolder.exists()) {
                Files.walk(summaryOutputFolder.toPath()).sorted(Comparator.reverseOrder())
                        .map(Path::toFile).forEach(File::delete);
            }
            LOG.info("Creating report folder [{}]", summaryOutputFolder.getAbsolutePath());
            rulesOutputFolder.mkdirs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSummary() {
        File file = new File(summaryOutputFolder, "index.md");

        LOG.info("Saving report to [{}]", file.getAbsolutePath());

        Map<String, Object> object = Map.of("date", new Date(), "violations", violations.entrySet()); // TODO Sort by value
        save(file, summaryTemplate, object, false);
    }

    @Override
    public void run() { // Call from shutdown hook
        saveSummary();
    }

    @Override
    public synchronized void onFailure(LimitReachedException limitReachedException) {

        String traceString = StackTraceUtils.stackTraceToString(limitReachedException);

        Rule rule = limitReachedException.getRule();
        String ruleId = rule.getId();

        int violationsCount = violations.getOrDefault(rule, 0) + 1;
        violations.put(rule, violationsCount);

        if (!shouldIgnoreTrace(ruleId, traceString)) {
            File file = new File(rulesOutputFolder, getFileName(ruleId));
            Map<String, Object> object = Map.of("rule", rule, "trace", traceString, "exception", limitReachedException);
            save(file, reportTemplate, object, true);
        }

    }

    private String getFileName(String ruleId) {
        return String.format("%s.md", ruleId);
    }

    private boolean shouldIgnoreTrace(String ruleId, String traceString) {
        int traceCode = traceString.hashCode();

        if (ignoreTraceDuplicates) {
            if ( traces.containsKey(traceCode) && traces.get(traceCode).contains(ruleId) ) {
                return true;
            } else {
                traces.computeIfAbsent(traceCode, (_k) -> new HashSet<>()).add(ruleId);
            }
        }

        return false;
    }

    private void save(File file, Mustache template, Map<String, Object> object, boolean append) {
        try {
            template.execute(new FileWriter(file, append), object).flush();
        } catch (IOException e) {
            LOG.error("Unable to save report", e);
        }
    }

}
