package up.visulog.analyzer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    private static String pwd;


    static {
        try {
            pwd = RInvocation.pwd();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPwd() {
        return pwd;
    }

    public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    public static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            String nom_mail = commit.getAuthor();
            String nom = nom_mail.split(" <")[0];
            String author=AuthorName(nom);
            var nb = result.commitsPerAuthor.getOrDefault(author, 0);
            result.commitsPerAuthor.put(author, nb + 1);
        }
        result.commitsPerAuthor = result.triAvecValeur(result.commitsPerAuthor);
        return result;
    }
    private static String AuthorName (String n){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(pwd+"/../analyzer/src/main/java/up/visulog/analyzer/AuthorName.txt")));
            String ligne;
            while((ligne = reader.readLine()) != null){
                String[] name = ligne.split("=");
                for(int i = 0;i < name.length;i++){
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
        long startTime=System.currentTimeMillis();
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath(), "log"));
        RInvocation invoke = new RInvocation();

            invoke.RGene(result,pwd+"/CommitsPerAuthor.R");
            invoke.RGene(result,pwd+"/CommitsPerAuthorPercent.R");
        System.out.println("[Visulog] Thread of CommitsPerAuthor plugin obtained in " + (System.currentTimeMillis()-startTime)/1000 +"s");
        /*try {
            Result.generateImageFromPDF("CommitsPerAuthor.pdf","png");
            Result.generateImageFromPDF("CommitsPerAuthorPercent.pdf","png");

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    public static class Result implements AnalyzerPlugin.Result {
        private Map<String, Integer> commitsPerAuthor = new HashMap<>();

        private static Map<String, Integer> triAvecValeur( Map<String, Integer> map ){
            List<Map.Entry<String, Integer>> list =
                    new LinkedList<Map.Entry<String, Integer>>( map.entrySet() );
            Collections.sort( list, new Comparator<Map.Entry<String, Integer>>(){
                public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ){
                    return (o2.getValue()).compareTo( o1.getValue());
                }
            });
            Map<String, Integer> map_apres = new LinkedHashMap<String, Integer>();
            for(Map.Entry<String, Integer> entry : list) {
                map_apres.put(entry.getKey(), entry.getValue());
            }
            return map_apres;
        }


        public Map<String, Integer> getCommitsPerAuthor() {
            return commitsPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return commitsPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {

        StringBuilder html = new StringBuilder("<div> <h1 onclick=\"toggle('showDiv2')\">Number of commits per author:</h1> <div id=\"showDiv2\" style=\"display:none;\"> <img src=\""+ pwd + "/.graphs/CommitsPerAuthor.pdf\"> <br><br>"+ "<img src=\""+ pwd + "/.graphs/CommitsPerAuthorPercent.pdf\">"  );
            html.append("<table id=\"commitsPerAuthor\"><tbody><thead><tr><th>Name</th><th>Commits count</th><th></th></thead>");
            int max=10;
            int cpt =0;
            for (var item : commitsPerAuthor.entrySet()) {
                if(cpt++== max){
                    html.append("</tbody>" + "</table>" +"<p onclick=\"toggle('showDiv2SousPartie')\"> suite :<p>" +"<div id=\"showDiv2SousPartie\" style=\"display:none;\">" + "<table id=\"commitsPerAuthor\">" +" <tbody>");
                }
                String nom_mail = item.getKey();
                String nom = nom_mail.split("<")[0];
                html.append(String.format("<tr><td>"+ nom + " </td><td id=\"commitsPerAuthorValue\"> " + item.getValue() + "</td>"));
            }
            html.append("</tbody></table></div></div></div>");
            return html.toString();
        }

        public String getRData() {
            StringBuilder R_txt = new StringBuilder();
            int max=10;
            int cpt =0;
            for (var item : commitsPerAuthor.entrySet()) {
                String nom_mail = item.getKey();
                String nom = nom_mail.split("<")[0].replace(" ", "_");
                if(cpt< max){
                    R_txt.append(String.format(nom.split("_")[nom.split("_").length-1] + " " + item.getValue() + "\n"));
                    cpt++;
                }
            }
            return R_txt.toString();
        }

        public String getLegende(){
            int i = 0;
            String res = "";
            for (var item : commitsPerAuthor.entrySet()) {
                res += "<p>";
                String nom_mail = item.getKey();
                String nom = nom_mail.split(" <")[0].replace(" ", "_");
                nom = nom.substring(0, nom.length()-1);
                res += nom + " : " + i + "\n" + "</p>";
                i++;
            }
            return res;
        }

        public void CreateRtxt(String s, String lien) throws IOException {
                File txt = new File(lien + "/commitsPA.txt" );
                boolean append = !txt.exists();
                FileOutputStream fos = new FileOutputStream(txt, append);
                fos.write(s.getBytes());
                fos.close();
        }
        public static void generateImageFromPDF(String filename, String extension) throws IOException {
            PDDocument document = PDDocument.load(new File(filename));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(
                        page, 700, ImageType.RGB);
                ImageIOUtil.writeImage(
                        bim, String.format(filename.substring(0,filename.indexOf('.')) +".%s", extension), 300);
            }
            document.close();
        }





    }

    public static void main(String[] args) throws IOException {
        //System.out.println(CountCommitsPerAuthorPlugin.Result.pwd());
        //RInvocation invoke = new RInvocation();
        //System.out.println(RInvocation.pwd()+"/analyzer/src/main/java/up/visulog/analyzer/AuthorName.txt");
       // AuthorName("lne.h");
///Users/hu/visulog/analyzer/src/main/java/up/visulog/analyzer

        //CountCommitsPerAuthorPlugin.Result.mkdir(".test");
        //CountCommitsPerAuthorPlugin.Result.mkdir(".test/test2");


    }
}

