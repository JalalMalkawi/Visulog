package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.GetGitCommandOutput;

import java.io.IOException;
import java.util.Collections;
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
        // FIXME : Doublon à la sortie…
        var output = new GetGitCommandOutput(configuration.getGitPath(),
                "log --pretty=format:%ad"
        );
        try {
            result = new Result();
            result.commitsPerHour = new LinkedList<String>();
            LinkedList<Integer> list_temp = new LinkedList<>();
            var getting = output.getOutput();

            String s = "";
            while ((s = getting.readLine()) != null) {
                String[] cut1 = s.split(" ");
                String cut2 = cut1[3].split(":")[0];
                System.out.println(cut2);
                list_temp.add(Integer.parseInt(cut2));
            }
            Collections.sort(list_temp);
            String st = "";
            for(int i = 0 ; i < list_temp.size() ; i++) {
                if(!st.contains(list_temp.get(i).toString())) {
                    int c = 0;
                    st += list_temp.get(i);
                    for (Integer integer : list_temp) {
                        if (list_temp.get(i).intValue() == integer.intValue()) c++;
                    }
                    result.commitsPerHour.add(c + " " + list_temp.get(i).toString());
                }
            }
            getting.close();
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
            if(commitsPerHour.isEmpty() || commitsPerHour==null) return html.append(" : Aucun commit</div>").toString();
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
