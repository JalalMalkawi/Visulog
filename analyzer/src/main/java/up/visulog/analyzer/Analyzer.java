package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Analyzer {
    private final Configuration config;
    private AnalyzerResult result;

    
    public Analyzer(Configuration config) {
        this.config = config;
    }

    public AnalyzerResult computeResults() {
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        for (var pluginConfigEntry: config.getPluginConfigs().entrySet()) {
            String pluginName = pluginConfigEntry.getKey();
            Optional<AnalyzerPlugin> plugin = makePlugin(pluginName);
            plugin.ifPresent(plugins::add);
        }
        // run all the plugins
        // TODO: try running them in parallel
        for (AnalyzerPlugin plugin: plugins) {
            //plugin.run();
            Thread t1 = new Thread(plugin);
            t1.start();
        }

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
    }

    // TODO: find a way so that the list of plugins is not hardcoded in this factory
    private Optional<AnalyzerPlugin> makePlugin(String pluginName) {
        switch (pluginName) {
            case "countCommits" : return Optional.of(new CountCommitsPerAuthorPlugin(config));
            case "countTotalCommits" : return Optional.of(new CountTotalCommitsPlugin(config));
            case "countAuthor" : return Optional.of(new CountAuthorPlugin(config));
            case "countCommitsPerDay" : return Optional.of(new CountCommitsPerDayPlugin(config));
            case "countCommitsPerHour" : return Optional.of(new CountCommitsPerHourPlugin(config));
            case "dailyAverage" : return Optional.of(new DailyAveragePlugin(config));
            case "countCommitsPerMonth" : return Optional.of(new CountCommitsPerMonthPlugin(config));
            case "countMergeCommits" : return Optional.of(new CountMergeCommitsPlugin(config));
            case "countModifiedLinesPerAuthor" : return Optional.of(new CountModifiedLinesPerAuthorPlugin(config));
            case "countTotalModifiedLines" : return Optional.of(new CountTotalModifiedLinesPlugin(config));
            case "countModifiedLinesPerDay" : return Optional.of(new CountModifiedLinesPerDayPlugin(config));
            case "countModifiedLinesPerAuthorPerDay" : return Optional.of(new CountModifiedLinesPerAuthorPerDayPlugin(config));
            default : return Optional.empty();
        }
    }
}
