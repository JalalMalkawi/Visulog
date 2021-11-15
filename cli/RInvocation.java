import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class RInvocation{
    
    public static void RGene(){
        
        try {
            mkdir(".visulogRTempFiles");
            //String s = res.getRdata();
            //res.getRtxt(s,pwd() + ".visulogRTempFiles")
            runWithR();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void runWithR(String nomFichier)throws IOException{
        Process process = new ProcessBuilder("R", "CMD" , "BATCH" , nomFichier , "result.txt").start();
    }
    
    public static void mkdir(String nom_dossier)throws IOException{
        Process process = new ProcessBuilder("mkdir", nom_dossier).start();
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
