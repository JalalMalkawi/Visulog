package up.visulog.analyzer;

import java.io.FileReader;
import java.io.IOException;

public interface AnalyzerPlugin extends Runnable {
    interface Result {
        String getResultAsString();
        String getResultAsHtmlDiv();
        String getRData();
        FileReader getRtxt(String s, String lien) throws IOException;
    }

    /**
     * run this analyzer plugin
     */
    void run();

    /**
     *
     * @return the result of this analysis. Runs the analysis first if not already done.
     */
    Result getResult();
}
