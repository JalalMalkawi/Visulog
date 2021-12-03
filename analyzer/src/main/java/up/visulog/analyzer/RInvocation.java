package up.visulog.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RInvocation{
    
    public static void RGene(AnalyzerPlugin.Result res, String nomFichier){
        try {
            mkdir(".visulogRTempFiles");
            //String s = res.getRData();
            //res.getRtxt(s,pwd() + "/.visulogRTempFiles");
            runWithR(nomFichier);
        } catch (Exception e) {
            System.out.println("[Visulog] Tried to run R but R not found");
        }
    }
    public static void runWithR(String nomFichier)throws IOException{
        new ProcessBuilder("R", "CMD" , "BATCH" , nomFichier , "result.txt").start();
    }
    
    public static void mkdir(String nom_dossier)throws IOException{
        new ProcessBuilder("mkdir", nom_dossier).start();
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
