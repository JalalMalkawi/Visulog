package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    public static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            var nb = result.commitsPerAuthor.getOrDefault(commit.getAuthor(), 0);
            result.commitsPerAuthor.put(commit.getAuthor(), nb + 1);
        }
        return result;
    }



    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), "log"));
        RInvocation invoke = new RInvocation();
        try {
            invoke.RGene(result,result.pwd()+"/CommitsPerAuthor.R");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();

        public Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            // <div><h1 onclick=\"toggle()\">Number of authors:</h1><div id=\"showDiv\">" + getResultAsString() +
            //                    "</div></div>
            String pwd = "";
            try {
                pwd = RInvocation.pwd();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder html = new StringBuilder("<div> <h1 onclick=\"toggle('showDiv2')\">Number of commits per author:</h1>" + "<iframe src=\""+pwd+"/.visulogRTempFiles/CommitsPerAuthor.pdf\" width=\"50%\"  height=\"530px\"></iframe>" + this.getLegende() +"<div id=\"showDiv2\"><ul>");
            for (var item : commitsPerAuthor.entrySet()) {
                String nom_mail = item.getKey();
                String nom = nom_mail.split("<")[0];
                String mail = nom_mail.split("<")[1].replaceAll(">", " ");
                html.append(String.format("<li><a href=\"mailto:" + mail + "\"> " + nom + "</a> : " + item.getValue() + "</li>"));
            }
            html.append("</ul></div></div>");
            return html.toString();
        }

        public String getRData() {
            StringBuilder R_txt = new StringBuilder();
            int i = 0;
            for (var item : commitsPerAuthor.entrySet()) {
                String nom_mail = item.getKey();
                String nom = nom_mail.split("<")[0].replace(" ", "_");
                nom = nom.substring(0, nom.length()-1);
                R_txt.append(String.format(Integer.toString(i) + " " + item.getValue() + "\n"));
                i++;
            }
            return R_txt.toString();
        }
        
        public String getLegende(){
            int i = 0;
            String res = "";
            for (var item : commitsPerAuthor.entrySet()) {
                res += "<p>";
                String nom_mail = item.getKey();
                String nom = nom_mail.split("<")[0].replace(" ", "_");
                nom = nom.substring(0, nom.length()-1);
                res += nom + " : " + Integer.toString(i) + "\n" + "</p>";
                i++;
            }
            return res;
        }

        public void getRtxt(String s, String lien) throws IOException {
                File txt = new File(lien + "/commitsPA.txt" );
                boolean append = !txt.exists();
                FileOutputStream fos = new FileOutputStream(txt, append);
                fos.write(s.getBytes());
                fos.close();
        }

        public static void runWithR(String nomFichier)throws IOException {
            Process process = new ProcessBuilder("R", "CMD", "BATCH", nomFichier, "result.txt").start();
        }
        
        
        public static void mkdir(String nom_dossier)throws IOException{
            Process process = new ProcessBuilder("mkdir", nom_dossier).start();
        }
        
        
        public static String pwd()throws IOException{
            Process process;
            ProcessBuilder builder = new ProcessBuilder("pwd");
            try {
                process = builder.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        }

    }

    public static void main(String[] args) throws IOException {
        System.out.println(CountCommitsPerAuthorPlugin.Result.pwd());
        RInvocation invoke = new RInvocation();
        System.out.println(RInvocation.pwd());

        //CountCommitsPerAuthorPlugin.Result.mkdir(".test");
        //CountCommitsPerAuthorPlugin.Result.mkdir(".test/test2");



    }
}

