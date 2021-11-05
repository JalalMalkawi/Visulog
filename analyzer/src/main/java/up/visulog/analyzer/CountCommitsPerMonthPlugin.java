package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.GetGitCommandOutput;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

public class CountCommitsPerMonthPlugin implements AnalyzerPlugin{
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerMonthPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }



    @Override
    public void run() {
        result =  aux();
    }

    private Result aux(){
        Result r0 = new Result();
        // On execute étape par étape la commande :
        // git log --date=format:'%Y-%m'  --pretty=format:%ad | sort | uniq -c
        var output = new GetGitCommandOutput(configuration.getGitPath(),
                "log --date=format:%Y-%m --pretty=format:%ad "
        );
        r0.commitsPerMonth = new LinkedList<String>();
        try {
            LinkedList<String> list_temp = new LinkedList<>();
            var getting = output.getOutput();
            String s = "";
            while ((s = getting.readLine()) != null) {
                list_temp.add(s);
            }
            Collections.sort(list_temp); // sort
            String st = "";
            for(int i = 0 ; i < list_temp.size() ; i++) { // uniq + count
                if(!st.contains(list_temp.get(i).toString())) {
                    int c = 0;
                    st += list_temp.get(i);
                    for (String str : list_temp) {
                        if (list_temp.get(i).equals(str)) c++;
                    }
                    r0.commitsPerMonth.add(c + " " + list_temp.get(i).toString());
                }
            }
            getting.close();
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
        private LinkedList<String> commitsPerMonth = new LinkedList<>();

        public LinkedList<String> getCommitsPerMonth() {
            return commitsPerMonth;
        }

        @Override
        public String getResultAsString() {
            return commitsPerMonth.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Commits Per Month");
            if(commitsPerMonth.isEmpty()) return html.append(" : No commit</div>").toString();
            html.append(" <table><tbody><thead><tr><th>Month </th><th>Commits count</th></thead>");
            for (String item : commitsPerMonth) {
                if(item!=null) {
                    html.append("<tr>");
                    String howMany = item.split(" ")[0];
                    String when = item.split(" ")[1];
                    html.append("<td>").append(when).append("</td>");
                    html.append("<td>").append(howMany).append("</td>");
                    html.append("</tr>");
                }
            }
            html.append("</tbody></table></div>");
            return html.toString();
        }
    }
}
