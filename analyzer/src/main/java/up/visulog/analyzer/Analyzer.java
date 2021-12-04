package up.visulog.analyzer;

import up.visulog.config.Configuration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class Analyzer {
    private final Configuration config;
    private AnalyzerResult result;

    
    public Analyzer(Configuration config) {
        this.config = config;
    }

    public AnalyzerResult computeResults() {
        System.out.println("[Visulog] Computing results of plugins...");
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        for (var pluginConfigEntry: config.getPluginConfigs().entrySet()) {
            String pluginName = pluginConfigEntry.getKey();
            Optional<AnalyzerPlugin> plugin = (Optional<AnalyzerPlugin>) makePlugin(pluginName);
            plugin.ifPresent(plugins::add);
        }
        // run all the plugins
        System.out.println("[Visulog] Running " + plugins.size() + " threads for plugins...");
        for (AnalyzerPlugin plugin: plugins) {
            Thread t1 = new Thread(plugin);
            t1.start();
        }

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
    }

    private Optional<?> makePlugin(String pluginName) {
        for(File f : new File("./../analyzer/src/main/java/up/visulog/analyzer/").listFiles()) { // never null
            if (f.getName().length()>11 && f.getName().substring(0, f.getName().length() - 11).toLowerCase(Locale.ROOT)
                    .equals(pluginName.toLowerCase(Locale.ROOT))) {
                System.out.println("[Visulog] Building plugin : " + pluginName);
                try {
                    //System.out.println("[Visulog] Plugin call : " + pluginName);
                    Class a = Class.forName("up.visulog.analyzer."+(String.valueOf(pluginName.charAt(0)).toUpperCase(Locale.ROOT) + pluginName.substring(1)+"Plugin"));
                    Class[] types = {config.getClass()};
                    Constructor constructor = a.getConstructor(types);
                    Object[] parameters = {config};
                    return Optional.of(constructor.newInstance(parameters));
                } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    System.out.println("[Visulog] A fatal error occurred while loading plugin, please submit us an issue at " +
                            "https://gaufre.informatique.univ-paris-diderot.fr/benmouff/visulog/issues" +
                            "\nOr send us an email at jeremybnfk@gmail.com");
                    System.exit(1);
                }
            }
        }
        return Optional.empty();
    }
}
