package up.visulog.analyzer;

import java.io.IOException;
import java.util.List;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.GetGitCommandOutput;

public class CountTotalModifiedLinesPlugin implements AnalyzerPlugin
{
    private final Configuration configuration;
    private Result result;

    public CountTotalModifiedLinesPlugin(Configuration config)
    {
        this.configuration = config;
    }


    public Result processLog(List<Commit> gitLog)
    {
        var res = new CountTotalModifiedLinesPlugin.Result();
        String id1;
        String id2;

        for(int i=0; i<gitLog.size()-1; i++)
        {
            id1 = gitLog.get(i+1).getId();
            id2 = gitLog.get(i).getId();
            
            res.sum += this.getLineDifference(id1, id2);
        }
        return res;
    }


    public int getLineDifference(String id1, String id2) 
    {
        String info = "";
        int res = 0;
        GetGitCommandOutput process = new GetGitCommandOutput(this.configuration.getGitPath(), "diff --shortstat "+id1+" "+id2);
        try {
            info = process.getOutput().readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error running git diff. ", e);
        }
        if(info==null)
        {
            return res;
        }
        
        String[] temp = info.split(", ");
        if(temp.length==2)
        {
            res = Integer.valueOf(temp[1].split(" ")[0]);
        } 
        else
        {
            res = Integer.valueOf(temp[1].split(" ")[0]);
            res += Integer.valueOf(temp[2].split(" ")[0]);
        }        
        return res;
    }
    
    @Override
    public void run() 
    {
        this.result = this.processLog(Commit.parseLogFromCommand(this.configuration.getGitPath(),"log"));
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
        private int sum;

        @Override
        public String getResultAsString() 
        {
            return String.valueOf(sum);
        }

        @Override
        public String getResultAsHtmlDiv() 
        {
            return "<div><h1>Number of modified lines since start:</h1>" + getResultAsString() +"</div>";
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
