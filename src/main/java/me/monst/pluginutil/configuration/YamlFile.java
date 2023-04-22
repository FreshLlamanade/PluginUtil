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

/**
 * Represents a YAML file.
 * This will automatically copy over the plugin resource of the same name, if it exists, to the target path.
 * The file can be loaded and saved using SnakeYAML.
 */
public class YamlFile {
    
    private final Plugin plugin;
    private final Path path;
    private final Yaml yaml;
    
    /**
     * Creates a new YAML file at the given path. If the path does not end with {@code .yml}, it will be appended.
     * @param plugin the plugin
     * @param path the path
     */
    public YamlFile(Plugin plugin, Path path) {
        this.plugin = plugin;
        this.path = path.toString().endsWith(".yml") ? path : path.resolveSibling(path.getFileName() + ".yml");
        this.yaml = createYaml();
    }
    
    /**
     * Creates a new YAML file with the given filename in the plugin data folder.
     * @param plugin the plugin
     * @param filename the filename
     */
    public YamlFile(Plugin plugin, String filename) {
        this(plugin, plugin.getDataFolder().toPath().resolve(filename));
    }
    
    private Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }
    
    /**
     * Copies the default file from the plugin resources to the target path, if the target file does not exist yet.
     * If the default file does not exist in the plugin resources, an empty file is created instead.
     */
    private void copyDefaultFile() {
        InputStream in = plugin.getResource(path.getFileName().toString());
        try {
            Files.createDirectories(plugin.getDataFolder().toPath());
            if (in != null)
                Files.copy(in, path);
            else
                Files.createFile(path);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to copy " + path.getFileName() + " to " + path.getParent() + "!");
        }
    }
    
    /**
     * Loads the file using SnakeYAML.
     * @return the loaded object, or {@code null} if the file could not be loaded
     */
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
    
    /**
     * Saves the given object to the file using SnakeYAML.
     * @param object the object to save
     */
    public void save(Object object) {
        try (Writer out = Files.newBufferedWriter(path)) {
            Files.createDirectories(path.getParent());
            yaml.dump(object, out);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save changes to " + path.getFileName() + "!");
        }
    }
    
}
