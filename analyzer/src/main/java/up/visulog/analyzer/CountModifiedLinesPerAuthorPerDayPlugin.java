package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CountModifiedLinesPerAuthorPerDayPlugin implements AnalyzerPlugin
{

    private final Configuration configuration;
    private Result result;

    public CountModifiedLinesPerAuthorPerDayPlugin(Configuration config)
    {
        this.configuration = config;
    }

    public long getTotalDays()
    {
        List<Commit> list = Commit.parseLogFromCommand(this.configuration.getGitPath(),"log");
        return DailyAveragePlugin.timeFormFirstCommit(list.get(list.size()-1));
    }

    public HashMap<String,Double> getLinesPerAuthor()
    {
        HashMap<String,Integer[]> temp = (new CountModifiedLinesPerAuthorPlugin(this.configuration)).getResult().getResultAsHashMap();
        HashMap<String,Double> res = new HashMap<String,Double>();
        for(Map.Entry<String,Integer[]> i:temp.entrySet())
        {
            res.put(i.getKey(), Double.valueOf((double)(i.getValue()[0].intValue()+i.getValue()[1].intValue())));
        }
        return res;

    }

    public Result getAverageLines()
    {
        var res = new CountModifiedLinesPerAuthorPerDayPlugin.Result();
        long days = this.getTotalDays();
        HashMap<String,Double> temp = getLinesPerAuthor();
        for(Map.Entry<String,Double> i:temp.entrySet())
        {
            res.modifiedLinesPerAuthor.put(i.getKey(), Double.valueOf(i.getValue().doubleValue()/days));
        }
        return res;
    }

    @Override
    public void run() 
    {
        long startTime=System.currentTimeMillis();
        this.result = this.getAverageLines();
        System.out.println("[Visulog] Thread of ModifiedLinesPerAuthorPerDay plugin obtained in " + (System.currentTimeMillis()-startTime)/1000 +"s");
    }

    @Override
    public Result getResult() 
    {
        if(this.result==null)
            this.run();
        return this.result;
    }

    public static class Result implements AnalyzerPlugin.Result
    {
        private HashMap<String,Double> modifiedLinesPerAuthor = new HashMap<String,Double>();

        @Override
        public String getResultAsString() 
        {
            String res = "";
            for(Map.Entry<String,Double> i:modifiedLinesPerAuthor.entrySet())
            {
                res = res + i.getKey() +" : "
                          + String.format("%.2f", i.getValue().doubleValue()) +" modified lines per day."
                          + "\n";
            }
            return res;
        }

        @Override
        public String getResultAsHtmlDiv() 
        {
            StringBuilder html = new StringBuilder("<div><h1 onclick=\"toggle('showDiv7')\">Modified lines per author per day: </h1>");
            if(modifiedLinesPerAuthor.isEmpty()) 
            {
                return html.append("None</div>").toString();
            }
            html.append(" <div id=\"showDiv7\"  style =\"display:none;\"><table><tbody><thead><tr><th>Author</th><th>Added+deleted lines per day</th></thead>");

            for (Entry<String,Double> item : modifiedLinesPerAuthor.entrySet()) {
                if(item!=null) 
                {
                    html.append("<tr>");
                    html.append("<td>").append(item.getKey()).append("</td>");
                    html.append("<td>").append(String.format("%.3f",item.getValue().doubleValue())).append("</td>");
                    html.append("</tr>");
                }
            }
            html.append("</tbody></table></div></div>");
            return html.toString();
        }

        @Override
        public String getRData() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void CreateRtxt(String s, String lien) throws IOException {
            // TODO Auto-generated method stub
        }
        
    }
    
}
