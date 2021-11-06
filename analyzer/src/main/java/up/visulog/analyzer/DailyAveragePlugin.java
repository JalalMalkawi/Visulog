package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class DailyAveragePlugin implements AnalyzerPlugin{

    private final Configuration configuration;
    private Result result;

    private static Map<String, Long> totalTime=new HashMap<>();

    public DailyAveragePlugin(Configuration generalConfiguration){

        this.configuration=generalConfiguration;
    }

    public static long daysBetween(Calendar calCurrent, Calendar calFirstCom){

        long timeCurrent=calCurrent.getTimeInMillis();
        long timeFirstCom=calFirstCom.getTimeInMillis();
        return (timeCurrent-timeFirstCom)/86400000; //1000*60*60*24
    }

    public static int stringToMonth(String s){

        int month=0;

        switch(s){
            case "Jan": month=1; break;
            case "Feb": month=2; break;
            case "Mar": month=3; break;
            case "Apr": month=4; break;
            case "May": month=5; break;
            case "Jun": month=6; break;
            case "Jul": month=7; break;
            case "Aug": month=8; break;
            case "Sep": month=9; break;
            case "Oct": month=10; break;
            case "Nov": month=11; break;
            case "Dec": month=12; break;
        }

        return month;

    }

    public static long timeFormFirstCommit(Commit commit){

        String[] date=commit.getDate().split(" ");

        Calendar calCurrent=Calendar.getInstance();
        Calendar calFirstCom=Calendar.getInstance();
        calFirstCom.set(Integer.valueOf(date[4]), stringToMonth(date[1]), Integer.valueOf(date[2]));

        return Math.abs(daysBetween(calCurrent, calFirstCom));

    }

    public static Map<String, Integer> comPerAuth(List<Commit> gitLog){

        Map<String, Integer> aux=new HashMap<>();

        for (var commit:gitLog) {
            var nb=aux.getOrDefault(commit.getAuthor(), 0);
            if(nb==0) totalTime.put(commit.getAuthor(), timeFormFirstCommit(commit));
            aux.put(commit.getAuthor(), nb+1);
        }

        return aux;

    }

    public static BigDecimal calculAverage(int commits, String key){

        BigDecimal bd1=new BigDecimal(commits);
        BigDecimal bd2=new BigDecimal(totalTime.get(key));
        return bd1.divide(bd2, 3, BigDecimal.ROUND_HALF_UP);

    }

    public static Result processLog(Map<String, Integer> commitsPerAuthor){

        var result = new Result();

        for (var commit:commitsPerAuthor.entrySet())
            result.dailyAverage.put(commit.getKey(), calculAverage(commit.getValue(), commit.getKey()));

        return result;

    }

    @Override
    public void run(){

        result=processLog(comPerAuth(Commit.parseLogFromCommand(configuration.getGitPath(),"log"))); 

    }

    @Override
    public Result getResult() {

        if (result==null) run();
        return result;

    }

    public static class Result implements AnalyzerPlugin.Result {

        private final Map<String, BigDecimal> dailyAverage=new HashMap<>();

        @Override
        public String getResultAsString(){

            return String.valueOf(dailyAverage);

        }

        @Override
        public String getResultAsHtmlDiv(){

            StringBuilder html=new StringBuilder("<div> <h1 onclick=\"toggle('showDiv3')\">Daily Average Per Author:</h1> <div id=\"showDiv3\"><ul>");

            for (var item : dailyAverage.entrySet()) {
                String nom_mail = item.getKey();
                String nom = nom_mail.split("<")[0];
                String mail = nom_mail.split("<")[1].replaceAll(">"," ");
                html.append(String.format("<li><a href=\"mailto:"+mail+"\"> "+nom+"</a> : "+item.getValue()+"</li>"));
            }
            html.append("</ul></div></div>");

            return html.toString();

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
