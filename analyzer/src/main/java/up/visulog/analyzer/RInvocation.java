package up.visulog.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;


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
    
    public static void cleanUp(boolean supprStackTrace){
        try{
            java.lang.Thread.sleep(1000); //attend une seconde avant de supprimer les fichiers
            try{
            File f1 = new File(pwd() + "/.visulogRTempFiles");
            for (File f : f1.listFiles()){
                f.delete();
            }
            f1.delete();
            if (supprStackTrace){
                File f2 = new File(pwd() + "/CommitsPerAuthorPercentResult.txt");
                f2.delete();
                File f3 = new File(pwd() + "/CommitsPerAuthorResult.txt");
                f3.delete();
                File f4 = new File(pwd() + "/CommitsPerDateResult.txt");
                f4.delete();
            }
        }catch(IOException e){
            System.out.println(".visulogRTempFiles does not exist.");
        }
        }catch (InterruptedException e){
            System.out.println("Error with the deletion of visulog/cli/.visulogRTempFiles ");
        }
    
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
