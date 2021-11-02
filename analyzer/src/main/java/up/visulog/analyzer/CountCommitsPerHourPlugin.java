package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.GetGitCommandOutput;

import java.io.IOException;
import java.util.LinkedList;

public class CountCommitsPerHourPlugin implements AnalyzerPlugin{
    //  log --date=local --pretty=format:%ad | cut -d\  -f4 | cut -d\: -f1 | sort | uniq -c
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerHourPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }



    @Override
    public void run() {
        var output = new GetGitCommandOutput(configuration.getGitPath(),
                "log --pretty=format:%ad | cut -d\\  -f4 | cut -d\\: -f1 | sort | uniq -c | sed 's/^ *//'"
        );
        try {

            result = new Result();
            result.commitsPerHour = new LinkedList<>();
            var getting = output.getOutput(); // .getOutputStream();



            String s ="";
            while ((s = getting.readLine()) != null) {
                System.out.println(s);
                result.commitsPerHour.add(s);
            }
        } catch (IOException e) {
            // TODO : gérer exception
            e.printStackTrace();
        }
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private LinkedList<String> commitsPerHour = new LinkedList<>();

        public LinkedList<String> getCommitsPerHour() {
            return commitsPerHour;
        }

        @Override
        public String getResultAsString() {
            return commitsPerHour.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Commits Per Hour");
            if(commitsPerHour.isEmpty()) return html.append(" : Aucun commit</div>").toString();
            html.append(" <table><tbody><thead><tr><th>Heure</th><th>Nombre de commits</th></thead>");
            for (String item : commitsPerHour) {
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
