package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.List;
import java.util.LinkedList;

public class CountAuthorPlugin implements AnalyzerPlugin{
    private final Configuration configuration;
    private CountAuthorPlugin.Result result;

    public CountAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    public static CountAuthorPlugin.Result processLog(List<Commit> gitLog) {
        var result = new Result();
        LinkedList<String> listAuthor= new LinkedList<String>();
        for (var commit : gitLog) {
           if(!listAuthor.contains(commit.getAuthor())) listAuthor.add(commit.getAuthor());
        }
        result.sum=listAuthor.size();
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),"log"));
    }

    @Override
    public CountAuthorPlugin.Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private int sum;
        @Override
        public String getResultAsString() {
            return String.valueOf(sum);
        }

        @Override
        public String getResultAsHtmlDiv() {
            return "<div>Number of authors: " + getResultAsString() +
                    "</div>";
        }
    }
}