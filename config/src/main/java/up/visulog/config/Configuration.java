package up.visulog.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Configuration {

    private final Path gitPath;
    private final Set<String> plugins;  //construit un dico "plugins" qui prend en entrée des clef String et les associe à des objet de type PluginConfig. Utilisation :
            //plugins.keySet() --> renvoie les clefs de plugins
            //plugins.values() --> renvoie les valeurs de plugins
            //plugins.entryset() --> renvoie les enrées de plugins

    public Configuration(Path gitPath, Set<String> plugins) {
        this.gitPath = gitPath;
        this.plugins = Set.copyOf(plugins);
    }

    public Path getGitPath() {
        return gitPath;
    }

    public Set<String> getPluginConfigs() {
        return plugins;
    }
}
