package up.visulog.analyzer;

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

    public String toHTML() { // insertion de l'invocation du code css
        return "<html><head><link rel=\"stylesheet\" href=\"result.css\" /><head><body>"+subResults.stream().map(AnalyzerPlugin.Result::getResultAsHtmlDiv).reduce("", (acc, cur) -> acc + cur) + "</body></html>";
    }

}
