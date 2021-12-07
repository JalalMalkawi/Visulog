package up.visulog.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;

public class RInvocation{
    private static String pwd="";
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
        //System.out.println(nomFichier);
        new ProcessBuilder("R", "CMD" , "BATCH" , nomFichier , s).start();
    }
    
    public static void mkdir(String nom_dossier)throws IOException{
        new ProcessBuilder("mkdir", nom_dossier).start();
    }
    
    public static void cleanUp(boolean supprStackTrace){
            File f1 = new File(pwd() + "/.visulogRTempFiles");
            if(f1.listFiles()==null) return;
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
                File f5 = new File(pwd() + "/CommitsPerHourResult.txt");
                f5.delete();
                File f6 = new File(pwd() + "/CommitsPerHourPercentResult.txt");
                f6.delete();
                File f7 = new File(pwd() + "/CommitsPerMonthPercentResult.txt");
                f7.delete();
                File f8 = new File(pwd() + "/CommitsPerMonthResult.txt");
                f8.delete();
            }

    }
    
    public static void cleanUpPdf(){
        File f1 = new File(pwd() + "/.graphs");
        if(f1.listFiles()==null) return;
        for (File f : f1.listFiles()){
            f.delete();
        }

    }
    
    
    
    public static String pwd(){
        if(pwd.equals("")){ pwd = generatePwd();}
        return pwd;
    }
    private static String generatePwd() {
        Process process;
        ProcessBuilder builder = new ProcessBuilder("pwd");
        String spaceEsc ="";
        try {
            System.out.println("[Visulog] Reading pwd");
            process = builder.start();
            spaceEsc = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
        } catch (IOException e) {
            System.out.println("[Visulog] ! Tried to read result of pwd command but can't");
            System.out.println("[Visulog] ! You may have no picture/graph on the result page");
        }
        for(int i = 0 ; i < spaceEsc.length()-1 ; i++){
            if(spaceEsc.charAt(i)==' '){
                spaceEsc = spaceEsc.substring(0,i)+"\\ " + spaceEsc.substring(i+1);
                i++;
            }
        }
        System.out.println(spaceEsc);
        return spaceEsc;
    }


    
    
    


}
