package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

public class CLILauncher {


    public static void main(String[] args) throws IOException {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            makeFileOfResAndOpenIt(results.toHTML()); // Sortie dans un fichier : visulog/cli/result.html
            System.out.println(results.toHTML());
        } else displayHelpAndExit();
        
    }

    public static void makeFileOfResAndOpenIt(String s) throws IOException {
        ProcessBuilder builder2 =
                new ProcessBuilder("touch", "result.html");
        builder2.start();
        ProcessBuilder builder =
                new ProcessBuilder("echo", "\"" +s + "\" > result.html");
        builder.start();
        FileOutputStream fos = new FileOutputStream("result.html");
        fos.write(s.getBytes());
        fos.flush();
        fos.close();
        //ProcessBuilder builder1 =
        //        new ProcessBuilder("open", "result.html");
        //builder1.start();
    }

    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashMap<String, PluginConfig>();
        for (var arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.split("=");
                if (parts.length != 2) return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                        case "--addPlugin":
                            // TODO: parse argument and make an instance of PluginConfig

                            // Let's just trivially do this, before the TODO is fixed:

                            if (pValue.equals("countCommits")) plugins.put("countCommits", new PluginConfig() {
                            });
                            if (pValue.equals("countTotalCommits")) plugins.put("countTotalCommits", new PluginConfig() {
                            });

                            break;
                        case "--loadConfigFile":
                            // TODO (load options from a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save command line options to a file instead of running the analysis)
                            break;
                        default:
                            return Optional.empty();
                    }
                }
            } else {
                gitPath = FileSystems.getDefault().getPath(arg);
            }
        }
        return Optional.of(new Configuration(gitPath, plugins));
    }

    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
        //TODO: print the list of options and their syntax
        System.exit(0);
    }
}
