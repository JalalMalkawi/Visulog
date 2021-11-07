package up.visulog.analyzer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.module.Configuration;

public interface AnalyzerPlugin extends Runnable {
    interface Result {
        String getResultAsString();
        String getResultAsHtmlDiv();
        String getRData();
        FileReader getRtxt(String s) throws IOException;
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
