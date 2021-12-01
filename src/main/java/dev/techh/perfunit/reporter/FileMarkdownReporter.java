package dev.techh.perfunit.reporter;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import dev.techh.perfunit.collector.InvocationsInfo;
import dev.techh.perfunit.collector.PerfUnitStorage;
import dev.techh.perfunit.configuration.data.Rule;
import dev.techh.perfunit.exception.LimitReachedException;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileMarkdownReporter implements Reporter, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Mustache ruleTemplate;
    private final Mustache summaryTemplate;
    private final Mustache ruleHeaderTemplate;

    private final File summaryOutputFolder;
    private final File rulesOutputFolder;

    private String folder = "./perfunit-report/";

    private PerfUnitStorage storage;

    public FileMarkdownReporter(PerfUnitStorage storage) {
        this.storage = storage;

        Runtime.getRuntime().addShutdownHook(new Thread(this) );

        summaryOutputFolder = new File(folder);
        rulesOutputFolder = new File(summaryOutputFolder, "rules");

        MustacheFactory mustacheFactory = new DefaultMustacheFactory();

        ruleHeaderTemplate = mustacheFactory.compile("template/markdown/rule-header.mustache");
        ruleTemplate = mustacheFactory.compile("template/markdown/rule.mustache");
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

        Map<Rule, Integer> violationsPerRule = storage.getViolationsPerRule();
        List<Map.Entry<Rule, Integer>> entries =
                violationsPerRule.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

        Map<Rule, InvocationsInfo> invocationsPerRule = storage.getInvocationsPerRule();

        Map<String, Object> object = Map.of("date", new Date(), "violations", entries, "invocations", invocationsPerRule.entrySet());
        save(file, summaryTemplate, object, false);
    }

    private void saveRules() {

        Map<Rule, Integer> violationsPerRule = storage.getViolationsPerRule();
        Map<Rule, Map<Long, Integer>> violationsPerStack = storage.getViolationsPerStack();
        Map<Rule, InvocationsInfo> invocationsPerRule = storage.getInvocationsPerRule();

        for ( Rule rule : violationsPerRule.keySet() ) {

            String ruleId = rule.getId();
            File file = new File(rulesOutputFolder, getFileName(ruleId));

            save(file, ruleHeaderTemplate, Map.of("rule", rule,
                    "invocations", invocationsPerRule.get(rule), "totalViolations", violationsPerRule.size()), true);

            Map<Long, Integer> violationPerStack = violationsPerStack.get(rule);

            violationPerStack.entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .forEach( ( entry ) -> {
                        Long traceId = entry.getKey();
                        Integer violations = entry.getValue();
                        String traceString = storage.getStackTrace(traceId);

                        Map<String, Object> object = Map.of("trace", traceString, "traceId", traceId, "count", violations);
                        save(file, ruleTemplate, object, true);

                    });

        }

    }

    @Override
    public void run() { // Call from shutdown hook
        saveSummary();
        saveRules();
    }

    @Override
    public void onFailure(LimitReachedException limitReachedException) { }

    private String getFileName(String ruleId) {
        return String.format("%s.md", ruleId);
    }

    private void save(File file, Mustache template, Map<String, Object> object, boolean append) {
        try {
            template.execute(new FileWriter(file, append), object).flush();
        } catch (IOException e) {
            LOG.error("Unable to save report", e);
        }
    }

}
