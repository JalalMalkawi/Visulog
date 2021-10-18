package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

//import java.util.HashMap;
import java.util.List;
//import java.util.Map;

public class CountTotalCommitsPlugin implements AnalyzerPlugin{
    private final Configuration configuration;
    private CountTotalCommitsPlugin.Result result;

    public CountTotalCommitsPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    public static CountTotalCommitsPlugin.Result processLog(List<Commit> gitLog) {
        var result = new CountTotalCommitsPlugin.Result();
        result.sum = gitLog.size();;
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),"log"));
    }

    @Override
    public CountTotalCommitsPlugin.Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private int sum;

        @Override
        public String getResultAsString() {
            return String.valueOf(sum);
        }

        @Override
        public String getResultAsHtmlDiv() {
            return "<div>Total commits: " + getResultAsString() +
                    "</div>";
        }
    }
}
