package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.GetGitCommandOutput;

import java.io.IOException;
import java.util.LinkedList;

public class CountMergeCommitsPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private CountMergeCommitsPlugin.Result result;

    public CountMergeCommitsPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }


    @Override
    public void run() {
        result=aux();
    }


    private Result aux(){
        // On execute étape par étape la commande :
        // git log --grep=Merge --pretty=oneline | wc -l
        Result r0= new Result();
        var output = new GetGitCommandOutput(configuration.getGitPath(),
                "log --grep=Merge --pretty=oneline"
        );
        try {
            LinkedList<String> list_temp = new LinkedList<>();
            var getting = output.getOutput();
            String s = "";
            while ((s = getting.readLine()) != null) {
                list_temp.add(s);
            }
            r0.sum=list_temp.size(); // wc-l
        } catch (IOException ignored) { // ignored car rendra une liste null
        }
        return r0;
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private int sum;

        public int getSum() {
            return sum;
        }

        @Override
        public String getResultAsString() {
            return String.valueOf(sum);
        }

        @Override
        public String getResultAsHtmlDiv() {
            return "<div><h1>Number of merge commits:</h1>" + getResultAsString() +
                    "</div>";
        }

        @Override
        public void getRtxt(String s, String lien) throws IOException {
        }

        @Override
        public String getRData() {
            return null;
        }
    }
}
