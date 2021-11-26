package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

public class CountAuthorPlugin implements AnalyzerPlugin{
    private final Configuration configuration;
    private CountAuthorPlugin.Result result;

    public CountAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    public static CountAuthorPlugin.Result processLog(List<Commit> gitLog) {
        var result = new CountAuthorPlugin.Result();
        LinkedList<String> listAuthor= new LinkedList<String>();
        for (var commit : gitLog) {
            String nom_mail = commit.getAuthor();
            String nom = nom_mail.split(" <")[0];
            String author=AuthorName(nom);
           if(!listAuthor.contains(author)) listAuthor.add(author);
        }
        result.sum=listAuthor.size();
        return result;
    }
    private static String AuthorName (String n){
        try {
            String pwd = RInvocation.pwd()+"/../analyzer/src/main/java/up/visulog/analyzer/AuthorName.txt";
            BufferedReader reader = new BufferedReader(new FileReader(new File(pwd)));
            String ligne;
            while((ligne = reader.readLine()) != null){
                String[] name = ligne.split("=");
                for(int i = 0;i<name.length;i++){
                    if (name[i].equals(n)) return name[0];
                }
            }
            return n;
        } catch (Exception ex){
            System.err.println("Error. "+ex.getMessage());
        }
        return "";
    }

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(),"log"));
    }

    @Override
    public CountAuthorPlugin.Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private int sum;
        @Override
        public String getResultAsString() {
            return String.valueOf(sum);
        }

        @Override
        public String getResultAsHtmlDiv() {
            return "<div><h1>Number of authors:</h1><div>" + getResultAsString() + "</div></div>";
        }

        @Override
        public String getRData() {
            return null;
        }

        @Override
        public void getRtxt(String s, String lien) throws IOException {
        }
    }
}
