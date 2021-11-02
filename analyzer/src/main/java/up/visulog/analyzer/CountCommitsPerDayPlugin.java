package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerDayPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerDayPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    public static CountCommitsPerDayPlugin.Result processLog(List<Commit> gitLog) {
        var result = new CountCommitsPerDayPlugin.Result();
        for (var commit : gitLog) {
            var nb = result.commitsPerDay.getOrDefault(commit.getDate(), 0);
            result.commitsPerDay.put(commit.getDate(), nb + 1);
        }
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),"log"));
    }

    @Override
    public CountCommitsPerDayPlugin.Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsPerDay = new HashMap<>();

        public Map<String, Integer> getCommitsPerDay() {
            return commitsPerDay;
        }

        @Override
        public String getResultAsString() {
            return commitsPerDay.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Commits Per Date <ul>");
            for (var item : commitsPerDay.entrySet()) {
                String date = item.getKey();
                String dateF = date.split("<")[0];
                //html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
                html.append("<li>").append(dateF).append(" &lt;").append("&gt; ").append(": ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}
