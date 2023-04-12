package me.monst.pluginutil.configuration;

import me.monst.pluginutil.command.Arguments;
import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.MissingValueException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;
import me.monst.pluginutil.configuration.transform.Transformer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A configuration value stored in a yaml file.
 * Every configuration value has a default value, which is used when no other value is specified.
 * The value can be changed at runtime by calling {@link #feed(Object)}.
 * @param <T> the type of the value
 */
public class ConfigurationValue<T> extends ConfigurationNode {

    private final T defaultValue;
    private T value;
    private final Transformer<T> transformer;
    private final Set<T> history = new LinkedHashSet<>();

    /**
     * Creates a new configuration value at the specified path in the plugin's configuration file. Calling this
     * constructor will immediately load the value from the file, creating it if it doesn't exist.
     *
     * @param key          the relative path of this value
     * @param defaultValue the default value of this configuration value
     * @param transformer  the transformer used for converting the data to and from the desired type
     */
    public ConfigurationValue(String key, T defaultValue, Transformer<T> transformer) {
        super(key);
        this.defaultValue = defaultValue;
        this.value = addToHistory(defaultValue);
        this.transformer = transformer;
    }
    
    private T addToHistory(T value) {
        history.remove(value); // Remove first to ensure that the most recent value is at the end
        history.add(value);
        return value;
    }
    
    /**
     * Gets the current value of this configuration value.
     * @return the current value
     */
    public final T get() {
        return value;
    }
    
    /**
     * Gets the default value of this configuration value as defined in the constructor.
     * @return the default value of this configuration value
     */
    public final T getDefaultValue() {
        return defaultValue;
    }
    
    public final Transformer<T> getTransformer() {
        return transformer;
    }
    
    @Override
    protected final void feed(Object object) {
        try {
            transformer.nullCheck(object);
            this.value = addToHistory(transformer.convert(object));
        } catch (ValueOutOfBoundsException e) {
            this.value = addToHistory(e.getReplacement());
        } catch (MissingValueException | UnreadableValueException e) {
            this.value = addToHistory(defaultValue);
        }
    }
    
    /**
     * Parses a user-entered string to a new value, and sets this configuration value.
     * <p><b>Note:</b></p> unlike other methods, this method automatically calls {@link Plugin#reloadConfig()} before and
     * {@link Plugin#saveConfig()} after performing the set operation, under the assumption that
     * parsing user input will not happen inside a loop.
     * Changes will be reflected in the {@code config.yml} file immediately.
     * @param input user input to be parsed, null if the value should be reset
     * @throws ArgumentParseException if the input could not be parsed
     */
    public final void feed(String input) throws ArgumentParseException {
        T value = transformer.parse(input);
        beforeSet();
        this.value = addToHistory(value);
        afterSet();
    }
    
    @Override
    protected final Object getAsYaml() {
        return transformer.toYaml(value);
    }

    /**
     * An action to be taken before every time this configuration value is set to a new value.
     */
    protected void beforeSet() {

    }

    /**
     * An action to be taken after every time this configuration value is set to a new value.
     */
    protected void afterSet() {

    }

    /**
     * Returns true if this configuration value can be changed and take effect at runtime, without a server restart.
     * This method may be overridden at will; it is not used by the rest of this library.
     * @return true if this value can be hot-swapped at runtime, false if it requires a restart.
     */
    public boolean isHotSwappable() {
        return true;
    }

    /**
     * Gets a list of tab-completions to be shown to a player typing in a command.
     * By default, this returns a formatted list of the current value and the default value of this configuration value.
     * @param player the player typing in the command
     * @param args the arguments the player has typed so far
     * @return a list of tab-completions
     */
    public List<String> getTabCompletions(Player player, Arguments args) {
        return history.stream().map(transformer::format).collect(Collectors.toList());
    }

    /**
     * @return the formatted current state of this configuration value
     */
    @Override
    public final String toString() {
        return transformer.format(get());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationValue<?> that = (ConfigurationValue<?>) o;
        return this.getKey().equals(that.getKey()) && Objects.equals(this.defaultValue, that.defaultValue);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getKey(), defaultValue);
    }

}
