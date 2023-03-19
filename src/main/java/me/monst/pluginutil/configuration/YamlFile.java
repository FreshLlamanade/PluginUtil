package me.monst.pluginutil.configuration;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class YamlFile {
    
    private final Plugin plugin;
    private final Path path;
    private final Yaml yaml;
    
    public YamlFile(Plugin plugin, Path path) {
        this.plugin = plugin;
        this.path = path.toString().endsWith(".yml") ? path : path.resolveSibling(path.getFileName() + ".yml");
        this.yaml = createYaml();
    }
    
    public YamlFile(Plugin plugin, String filename) {
        this(plugin, plugin.getDataFolder().toPath().resolve(filename));
    }
    
    private Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }
    
    private void copyDefaultFile() {
        InputStream in = plugin.getResource(path.getFileName().toString());
        try {
            if (in != null)
                Files.copy(in, path);
            else
                Files.createFile(path);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to copy " + path.getFileName() + " to " + path.getParent() + "!");
        }
    }
    
    public Object load() {
        if (!Files.exists(path))
            copyDefaultFile();
        try (Reader in = Files.newBufferedReader(path)) {
            return yaml.load(in);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load " + path.getFileName() + "!");
        }
        return null;
    }
    
    public void save(Object object) {
        try (Writer out = Files.newBufferedWriter(path)) {
            Files.createDirectories(path.getParent());
            yaml.dump(object, out);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save changes to " + path.getFileName() + "!");
        }
    }
    
}
