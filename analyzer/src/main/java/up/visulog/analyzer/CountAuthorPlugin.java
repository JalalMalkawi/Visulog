package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
            String pwd = "../analyzer/src/main/java/up/visulog/analyzer/AuthorName.txt";
            BufferedReader reader = new BufferedReader(new FileReader(pwd));
            String ligne;
            while((ligne = reader.readLine()) != null){
                String[] name = ligne.split("=");
                for(int i = 0;i<name.length;i++){
                    if (name[i].equals(n)) return name[0];
                }
            }
            return n;
        } catch (Exception ex){
            System.err.println("[Visulog] ! Error (AuthorName2)");
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
            return "<div><h1>Number of authors:  " + getResultAsString() + "</h1></div>";
        }

        @Override
        public String getRData() {
            return null;
        }

        @Override
        public void CreateRtxt(String s, String lien) throws IOException {
        }
    }
}
