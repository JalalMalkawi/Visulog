package up.visulog.analyzer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import up.visulog.gitrawdata.GetGitCommandOutput;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class CountCommitsPerDayPlugin implements AnalyzerPlugin{
    private final Configuration configuration;
    private Result result;
    public CountCommitsPerDayPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }
    
    private static String pwd;
    static {
        pwd = RInvocation.pwd();
    }


    @Override
    public void run() {
        long startTime=System.currentTimeMillis();
        result =  aux();
        RInvocation invoke = new RInvocation();
        
        invoke.RGene(result,pwd+"/CommitsPerDate.R");

        
        System.out.println("[Visulog] Thread of CommitsPerDay plugin obtained in " + (System.currentTimeMillis()-startTime)/1000 +"s");
    }

    private Result aux(){
        Result r0 = new Result();
        // On execute étape par étape la commande :
        // git log --date=short --pretty=format:%ad | sort | uniq -c
        var output = new GetGitCommandOutput(configuration.getGitPath(),
                "log --date=short --pretty=format:%ad"
        );
        r0.commitsPerDay = new LinkedList<String>();
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
                    r0.commitsPerDay.add(c + " " + list_temp.get(i).toString());
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
            private LinkedList<String> commitsPerDay = new LinkedList<>();

            public LinkedList<String> getCommitsPerDay() {
            return commitsPerDay;
        }

        @Override
        public String getResultAsString() {
            return commitsPerDay.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div><h1 onclick=\"toggle('showDiv1')\">Commits Per Day:</h1> ");
            if(commitsPerDay.isEmpty()) return html.append(" No commit</div>").toString();
            html.append("<div id=\"showDiv1\" style=\"display:none;\" >");
            html.append("<img src=\""+ pwd + "/.graphs/CommitsPerDate.pdf\">");
            int count = 1;
            String r = "";
            for (var item : commitsPerDay) {
                String jour = item.split(" ")[1];
                r += jour.substring(0,7);
            }
            
            int mois = 0;
            String s = r.substring(mois,mois+7);
            while (new File(pwd + "/.graphs/CommitsPerDate_"+ (count) +".pdf").exists()){
                html.append("<p onclick=\"toggle('showgraph"+(count)+"')\"> "+s+" </p>");
                
                html.append("<graph"+(count)+" id=\"showgraph"+(count)+"\" style=\"display:none;\">");
                
                html.append("<embed src=\""+ pwd + "/.graphs/CommitsPerDate_"+ (count) +".pdf\">");
                
                html.append("</graph"+(count)+">");
                count++;
                
                while( s.equals(r.substring(mois,mois+7)) && mois+7<r.length()){
                    mois += 7;
                }
                s =r.substring(mois,mois+7);
            }
            html.append("<table><tbody><thead><tr><th>Commits count </th><th>Day</th></thead>");
            Iterator<String> list = commitsPerDay.descendingIterator(); // iterator permettant d'itérer une liste dans l'ordre inverse
            int max= 10;
            while (list.hasNext()){
                String item= list.next();
                if(commitsPerDay.size()-commitsPerDay.indexOf(item)== max+1){
                    html.append("</tbody>" + "</table>" +"<p onclick=\"toggle('showDiv1SousPartie')\"> suite :<p>"
                                +"<div id=\"showDiv1SousPartie\" style=\"display:none;\">" + "<table>" +" <tbody>");
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
            for (var item : commitsPerDay) {
                String jour = item.split(" ")[0];
                String nombreDeCommits = item.split(" ")[1];
                R_txt.append(nombreDeCommits + " "  + jour + "\n");
            }
            return R_txt.toString();
        }
        
        

        @Override
        public void CreateRtxt(String s, String lien) throws IOException {
                File txt = new File(lien + "/commitsPerDate.txt" );
                boolean append = !txt.exists();
                FileOutputStream fos = new FileOutputStream(txt, append);
                fos.write(s.getBytes());
                fos.close();
            }
        }

    }

