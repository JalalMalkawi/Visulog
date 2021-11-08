package up.visulog.cli;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

public class CLILauncher {


    public static void main(String[] args) throws IOException {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            makeFileOfResAndOpenIt(results.toHTML()); // Sortie dans un fichier : visulog/cli/result.html
        } else displayHelpAndExit();
    }



    public static void makeFileOfResAndOpenIt(String s) throws IOException {

        FileUtils.writeStringToFile(new File("result.html"), "");
        FileOutputStream fos = new FileOutputStream("result.html");
        fos.write(s.getBytes());
        fos.flush();
        fos.close();
        File htmlFile = new File("result.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
    }

    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) throws IOException {
        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashMap<String, PluginConfig>();
        String[] s = {"countCommits","countTotalCommits","countAuthor",
                      "countCommitsPerDay","countCommitsPerHour","dailyAverage",
                      "countCommitsPerMonth","countMergeCommits","countModifiedLinesPerAuthor",
                      "countTotalModifiedLines","countModifiedLinesPerDay"};
        if(args.length==0) for(String st : s) plugins.put(st, new PluginConfig() {});
        boolean opt=false;
        for (var arg : args) {
            if (arg.startsWith("--")) {
                opt=true;
                String[] parts = arg.split("=");
                if (parts.length != 2) return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                        case "--addPlugin":
                            // TODO: parse argument and make an instance of PluginConfig
                            for(String st : s) if(pValue.equals(st)) plugins.put(st, new PluginConfig() {});
                            break;
                        case "--loadConfigFile":
                            // TODO (load options froadd m a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save command line options to a file instead of running the analysis)
                            break;
                        default:
                            return Optional.empty();
                    }
                }
            } else {
                if(!opt){
                    for(String st : s) plugins.put(st, new PluginConfig() {});
                }
                // arg est ici le lien d'un repo git
                // COMMAND DE TEST : ./gradlew run --args="--addPlugin=countTotalCommits https://gitlab.com/edouardklein/falsisign"
                if (isValidGitUrl(arg)){
                    CLILauncher c = new CLILauncher();
                    try {
                        FileUtils.deleteDirectory(new File("../dataFromGit"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    c.CloneRep(arg);
                    gitPath = Paths.get("../dataFromGit");
                }
            }
        }
        return Optional.of(new Configuration(gitPath, plugins));
    }

    public static int getResponseCode(String urlString) throws IOException {
        URL u = new URL(urlString);
        HttpURLConnection huc =  (HttpURLConnection)  u.openConnection();
        huc.setRequestMethod("GET");
        huc.connect();
        return huc.getResponseCode();
    }

    public static void CheckUrl(String url) {
        try {
            new URL(url).toURI();
        }
        catch (MalformedURLException | URISyntaxException e) {
            System.out.print("An error occurred with git-repo link : please check if you typed it correctly");
        }
    }

    public static boolean isValidGitUrl(String url) throws IOException  {
        if(url.contains("gitlab.com") || url.contains("github.com") || url.contains("gaufre.informatique.univ-paris-diderot.fr") && getResponseCode(url) != 404){
            CheckUrl(url);
            return true;
        }
        return false;
    }


    public void CloneRep(String s){
        String cloneDirectoryPath = "../dataFromGit";
        try {
            Git.cloneRepository()
                    .setURI(s)
                    .setDirectory(Paths.get(cloneDirectoryPath).toFile())
                    .call();
        } catch (GitAPIException e) {
            System.out.println("An error occurred while cloning repository, please check your internet connexion");
            System.exit(0);
        }
    }

    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
        //TODO: print the list of options and their syntax
        System.exit(0);
    }
}
