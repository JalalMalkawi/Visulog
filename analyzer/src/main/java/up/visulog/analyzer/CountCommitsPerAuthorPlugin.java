package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    public static Result processLog(List<Commit> gitLog) { 
        var result = new Result();
        for (var commit : gitLog) {
            var nb = result.commitsPerAuthor.getOrDefault(commit.getAuthor(), 0);
            result.commitsPerAuthor.put(commit.getAuthor(), nb + 1);
        }
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),"log")); 
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();

        public Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Commits per author: <ul>");
            for (var item : commitsPerAuthor.entrySet()) {
                String nom_mail = item.getKey();
                String nom = nom_mail.split("<")[0];
                String mail = nom_mail.split("<")[1].replaceAll(">"," ");
                //html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
                //html.append("<li>").append(nom).append("<a href='https://example.com>").append(mail).append("</a>; ").append(": ").append(item.getValue()).append("</li>");
                html.append("<li>").append(nom).append(" &lt;").append(mail).append("&gt; ").append(": ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}
