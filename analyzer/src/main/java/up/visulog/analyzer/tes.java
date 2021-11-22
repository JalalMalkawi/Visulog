package up.visulog.analyzer;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class tes {
    public static void main(String[] args) throws IOException {
        String name = "lne.h";
        String author="";
        String nom = name.split("<")[0];
        //File f =new File("/Users/hu/visulog/analyzer/src/main/java/up/visulog/analyzer");
        System.out.println(readFile("lne.h"));

    }
    public static String  readFile(String n){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("./AuthorName.txt")));
            String ligne;
            while((ligne = reader.readLine()) != null){

                    String[] name = ligne.split("=");
                    for(int i = 1;i<name.length;i++){

                        if (name[i].equals(n)){
                            System.out.println( name[0]);
                        }
                    }
            }
            return n;
        } catch (Exception ex){
            System.err.println("Error. "+ex.getMessage());
        }
        return "";
    }

}
