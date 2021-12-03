package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

//import java.util.HashMap;
import java.io.IOException;
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
        result.sum = gitLog.size();
        return result;
    }

    @Override
    public void run() {
        long startTime=System.currentTimeMillis();
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),"log"));
        System.out.println("[Visulog] Thread of TotalCommits plugin obtained in " + (System.currentTimeMillis()-startTime)/1000 +"s");
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

        public int getSum() {
            return sum;
        }

        @Override
        public String getResultAsHtmlDiv() {
            return "<div><h1>Total commits:  " + getResultAsString() +
                    "</h1></div>";
        }

        @Override
        public String getRData() {
            return null;
        }

        @Override
        public void getRtxt(String s, String lien) throws IOException {
        }
    }
}
