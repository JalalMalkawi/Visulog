package up.visulog.analyzer;
import java.io.IOException;
import java.util.List;


public class AnalyzerResult {

    public List<AnalyzerPlugin.Result> getSubResults() {
        return subResults;
    }

    private final List<AnalyzerPlugin.Result> subResults;

    public AnalyzerResult(List<AnalyzerPlugin.Result> subResults) {
        this.subResults = subResults;
    }

    @Override
    public String toString() {
        return subResults.stream().map(AnalyzerPlugin.Result::getResultAsString).reduce("", (acc, cur) -> acc + "\n" + cur);
    }

    public String toHTML() throws IOException { // insertion de l'invocation du code css
        return "<html><head><meta charset=\"UTF-8\"><link rel=\"stylesheet\" href=\"result.css\" /><head><body><div class=\"title\">\n" +
                "\t\t\t\t<img src=\""+ RInvocation.pwd()+"/visulog.png\" style=\" height: 90px; width: 160px;\">\n" +
                "\t\t\t</div>"+ subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur) + "<script>\n" +
                "function toggle(w) {\n" +
                "  var x = document.getElementById(w);\n" +
                "  if (x.style.display === \"none\") {\n" +
                "    x.style.display = \"block\";\n" +
                "  } else {\n" +
                "    x.style.display = \"none\";\n" +
                "  }\n" +
                "}\n" +
                "</script>\n" +
                "\n</body></html>";
    }
    
    
    //public static void runWithR(String nomFichier)throws IOException{
    //    Process process = new ProcessBuilder("R", "CMD" , "BATCH" , nomFichier , "result.txt").start();
   // }

}
