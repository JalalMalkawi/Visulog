package up.visulog.cli;

import error.CustomError;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import up.visulog.analyzer.Analyzer;
import up.visulog.analyzer.RInvocation;
import up.visulog.config.Configuration;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;

public class CLILauncher {



    public static void main(String[] args) throws IOException {
        try { // Au cas d'un arrêt brutale du programme : dataFromGit n'aura pas été supprimer
            FileUtils.deleteDirectory(new File("../dataFromGit"));
        } catch (IOException ignored) {
            if(!new File("../dataFromGit").delete()) {
                CustomError ex = new CustomError("Unable to delete dataFromGit/ folder, please do it by yourself");
                System.exit(0);
            }
        }
        RInvocation.cleanUpPdf();
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            makeFileOfResAndOpenIt(results.toHTML()); // Sortie dans un fichier : visulog/cli/result.html
            try {
                System.out.println("[Visulog] Removing the cloned repository at visulog/dataFromGit/");
                FileUtils.deleteDirectory(new File("../dataFromGit"));
            } catch (IOException ignored) {
                System.out.println("[Visulog] !!! Tried to remove the the cloned repository at visulog/dataFromGit/, but didn't did, please do it by yourself\n[Visulog] Delete visulog/dataFromGit folder");
            }
            RInvocation.cleanUp(true);
        } else displayHelpAndExit();
    }



    public static void makeFileOfResAndOpenIt(String s) {

        try {
            FileUtils.writeStringToFile(new File("result.html"), "");
        } catch (IOException ignored) { // result.html always exists
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("result.html");
        } catch (FileNotFoundException ignored) { // same
        }
        try {
            fos.write(s.getBytes());
            System.out.println("[Visulog] Writing results...");
            fos.flush();
            fos.close();
        } catch (IOException ignored) { // results.toHTML() never null/empty
        }
        File htmlFile = new File("result.html");
        try {
            System.out.println("[Visulog] Opening results...");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            CustomError err = new CustomError("An error occurred while creating the error page, your default browser is not found " +
                    "or didn't launched");
            System.exit(0);
        }
        
    }

    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashSet<String>();
        String[] sAdvanced = {"countCommitsPerAuthor","countTotalCommits","countAuthor",
                      "countCommitsPerDay","countCommitsPerHour",//"dailyAverage",
                      "countCommitsPerMonth","countMergeCommits","countModifiedLinesPerAuthor",
                      "countTotalModifiedLines","countModifiedLinesPerDay","countModifiedLinesPerAuthorPerDay"};
        String[] sSimple = {"countCommitsPerAuthor"/*,"countTotalCommits","countAuthor",
                "countCommitsPerDay","countCommitsPerHour",//"dailyAverage",
                "countCommitsPerMonth","countMergeCommits"*/};
        if(args.length==0) {
            for (String st : sAdvanced) plugins.add(st); // default visulog case
            gitPath = Paths.get(".");
        }
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
                        case "--advancedMode":
                            // Analyse avancée
                            for(String st : sAdvanced) plugins.add(st);
                            break;
                        case "--addPlugin":
                            for(String st : sAdvanced){
                                System.out.println("[Visulog] Running plugin : " + parts[1]);
                                if(pValue.equals(st)) plugins.add(st);
                            }
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
                    // Analyse simple
                    for(String st : sSimple) plugins.add(st);
                }
                // arg est ici le lien d'un repo git
                if (isValidGitUrl(arg)){
                    CLILauncher c = new CLILauncher();
                    System.out.println("[Visulog] Cloning repository...");
                    long startTime=System.currentTimeMillis();
                    c.CloneRep(arg);
                    long takenTime=(System.currentTimeMillis()-startTime)/1000;
                    System.out.println("[Visulog] Repository cloned in " + (takenTime/60) + "m " +takenTime+"s");
                    gitPath = Paths.get("../dataFromGit");
                }else{
                    CustomError err = new CustomError("Error : please check the link of your git repository, " +
                            "we are running Visulog on this Visulog project");
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
            String s = "An error occurred with git-repo link : please check if you typed it correctly";
            System.out.print(s);
            CustomError err = new CustomError(s);
        }
    }

    public static boolean isValidGitUrl(String url) {
        try {
            if(url.contains("gitlab.com") || url.contains("github.com")
                    || url.contains("gaufre.informatique.univ-paris-diderot.fr") && getResponseCode(url) != 404){
                CheckUrl(url);
                return true;
            }
        } catch (IOException e) {
            CustomError err = new CustomError("An error occurred while accessing to your repository link, " +
                    "please check your internet connexion or if the distant server is aviable");
            System.exit(0);
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
            String st = "An error occurred while cloning repository, please check your internet connexion" +
                    " or if the repository is in private mode, see Errors Section of README.md for more details<br><br>Invalid link : " + s;
            System.out.println("[Visulog] Error with the link : " + s);
            CustomError err = new CustomError(st);
            System.exit(0);
        }
    }

    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
        //TODO: print the list of options and their syntax
        System.exit(0);
    }
}
