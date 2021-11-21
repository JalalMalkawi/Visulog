package up.visulog.analyzer;

import java.io.IOException;
import java.util.List;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountModifiedLinesPerDayPlugin implements AnalyzerPlugin
{
    private final Configuration configuration;
    private Result result;

    public CountModifiedLinesPerDayPlugin(Configuration config)
    {
        this.configuration = config;
    }

    public long getTotalLines()
    {
        return (long)Integer.valueOf(new CountTotalModifiedLinesPlugin(this.configuration).getResult().getResultAsString());
    }

    public Result getAverageLines()
    {
        Result res = new CountModifiedLinesPerDayPlugin.Result();
        List<Commit> list = Commit.parseLogFromCommand(this.configuration.getGitPath(),"log");
        res.average = (double)getTotalLines()/DailyAveragePlugin.timeFormFirstCommit(list.get(list.size()-1));
        return res;
    }

    @Override
    public void run() 
    {
        this.result = this.getAverageLines();
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
        private double average;

        @Override
        public String getResultAsString() 
        {
            return String.format("%.2f", average);
        }

        @Override
        public String getResultAsHtmlDiv() 
        {
            return "<div><h1>Average number of modified lines per day:</h1>" + getResultAsString() +"</div>";
        }

        @Override
        public String getRData() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void getRtxt(String s, String lien) throws IOException {
            // TODO Auto-generated method stub
        }
        
    }
    
}
