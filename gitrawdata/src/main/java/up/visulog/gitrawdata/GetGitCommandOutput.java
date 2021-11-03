package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class GetGitCommandOutput
{
    private ProcessBuilder builder;
    private String command;

    public GetGitCommandOutput(Path gitPath, String command)
    {
        if(!command.contains("|")){
            if(!command.contains("-")) {
                this.builder = new ProcessBuilder("git ", command).directory(gitPath.toFile());
            }else{
                command = "git "+command;
                String[] splited = command.split(" ");
                this.builder = new ProcessBuilder(splited).directory(gitPath.toFile());
            }
        }
        else{
            String[] cmd = {
                    "/bin/sh", "-c",    // invoke shell and then run inside it
                    "git "+ command
            };
            this.builder = new ProcessBuilder(cmd).directory(gitPath.toFile());
        }
        this.command = command;
    }

    public BufferedReader getOutput()
    {
        Process process;
        try {
            process = this.builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error running git "+ this.command+". ", e);
        }
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }
}
