package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.GetGitCommandOutput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class CountCommitsPerMonthPlugin implements AnalyzerPlugin{
    private final Configuration configuration;
    private Result result;
    private static String pwd;
    static {
        pwd = RInvocation.pwd();
    }

    public CountCommitsPerMonthPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }



    @Override
    public void run() {
        long startTime=System.currentTimeMillis();
        result =  aux();
        RInvocation invoke = new RInvocation();
        invoke.RGene(result,pwd+"/CommitsPerMonth.R");
        
        System.out.println("[Visulog] Thread of CommitsPerMonth plugin obtained in " + (System.currentTimeMillis()-startTime)/1000 +"s");

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
            StringBuilder html = new StringBuilder("<div><h1 onclick=\"toggle('showDiv4')\">Commits Per Month:</h1>");
            if(commitsPerMonth.isEmpty()) return html.append(" No commit</div>").toString();

            html.append(" <div id=\"showDiv4\" style=\"display:none;\"><embed src=\""+ pwd + "/.graphs/CommitsPerMonth.pdf\"width=\"500\" height=\"400\"><table><tbody><thead><tr><th>Commits count</th><th>Month</th></thead>");

            Iterator<String> list = commitsPerMonth.descendingIterator(); // iterator permettant d'itérer une liste dans l'ordre inverse
            int max= 3;
            while (list.hasNext()){
                String item= list.next();
                if(commitsPerMonth.size()-commitsPerMonth.indexOf(item)== max+1){
                    html.append("</tbody>" + "</table>" +"<p onclick=\"toggle('showDiv4SousPartie')\"> suite :<p>" +"<div id=\"showDiv4SousPartie\" style=\"display:none;\" >" + "<table>" +" <tbody>");
                }
               if(item!=null) {
                    html.append("<tr>");
                    String howMany = item.split(" ")[0];
                    String when = item.split(" ")[1];
                    html.append("<td>").append(howMany).append("</td>");
                    html.append("<td>").append(when).append("</td>");
                    html.append("</tr>");
                }
            }
            html.append("</tbody></table></div></div></div>");
            return html.toString();
        }

        @Override
        public String getRData() {
            StringBuilder R_txt = new StringBuilder();
            for (var item : commitsPerMonth) {
                String nombreDeCommits= item.split(" ")[0];
                String mois = item.split(" ")[1];
                R_txt.append(nombreDeCommits + " " + mois + "\n");
            }
            return R_txt.toString();
        }

        @Override
        public void CreateRtxt(String s, String lien) throws IOException {
            File txt = new File(lien + "/commitsPerMonth.txt" );
            boolean append = !txt.exists();
            FileOutputStream fos = new FileOutputStream(txt, append);
            fos.write(s.getBytes());
            fos.close();
        }
    }
}
