package error;

import org.apache.commons.io.FileUtils;
import up.visulog.analyzer.RInvocation;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomError {
    private String error="";

    public void generateError() {
        String st = null;
        st = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <link rel=\"stylesheet\" href=\"result.css\" />\n" +
                "    <title>Visulog : error</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"title\">\n\n" +
                "                \t\t\t\t<img src=\""+ RInvocation.pwd()+"/visulog.png\" style=\" height: 110px;\">\n" +
                "                \t\t\t</div>" +
                "<div id=\"err\">\n" +
                error +
                "</div>\n" +
                "</body>\n" +
                "</html>";
        try {
            FileUtils.writeStringToFile(new File("Errors.html"), "");
        } catch (IOException ignored) {
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("Errors.html");
            fos.write(st.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while creating the error page, please allow us to write files" +
                    "\nIf you already did, submit us an issue at https://gaufre.informatique.univ-paris-diderot.fr/benmouff/visulog/issues");
            System.exit(1);
        } catch (IOException ignored) {
        }
        try {
            Desktop.getDesktop().browse((new File("Errors.html")).toURI());
        } catch (IOException e) {
            System.out.println("An error occurred while creating the error page, your default browser is not found " +
                    "or didn't launched" +
                    "\nIf already fixed this, submit us an issue at https://gaufre.informatique.univ-paris-diderot.fr/benmouff/visulog/issues");
            System.exit(1);
        }
    }
    public CustomError(String error) {
        this.error = error;
        this.generateError();
    }
}
