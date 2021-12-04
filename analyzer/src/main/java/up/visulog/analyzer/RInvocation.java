package up.visulog.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RInvocation{
    
    public static void RGene(AnalyzerPlugin.Result res, String nomFichier){
        try {
            mkdir(".visulogRTempFiles");
            String s = res.getRData();
            res.CreateRtxt(s,pwd() + "/.visulogRTempFiles");
            runWithR(nomFichier);
            System.out.println("[Visulog] Running R");
        } catch (Exception e) {
            System.out.println("[Visulog] Tried to run R but R not found");
        }
    }
    public static void runWithR(String nomFichier)throws IOException{
        String s = nomFichier.substring(0,nomFichier.length()-2) + "Result.txt";
        new ProcessBuilder("R", "CMD" , "BATCH" , nomFichier , s).start();
    }
    
    public static void mkdir(String nom_dossier)throws IOException{
        new ProcessBuilder("mkdir", nom_dossier).start();
    }
    
//les fonctions rm ne fonctionnent pas. Aucune erreur mais les fichiers ne sont pas détruits !!?
    public static void rm(String nom_fichier)throws IOException{
        new ProcessBuilder("rm", nom_fichier).start();
    }
    
    public static void rm_D(String nom_dossier)throws IOException{
        new ProcessBuilder("rm","-d", nom_dossier).start();
    }
    
    public static void cleanUp()throws IOException{
        rm(pwd() + "/.visulogRTempFiles/*");
        rm_D(pwd() + "/.visulogRTempFiles");
    }
    
    
    public static String pwd()throws IOException{
        Process process;
        ProcessBuilder builder = new ProcessBuilder("pwd");
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
    }
    
    
    


}
