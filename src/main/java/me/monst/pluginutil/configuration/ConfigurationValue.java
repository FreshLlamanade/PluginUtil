package me.monst.pluginutil.configuration;

import me.monst.pluginutil.command.Arguments;
import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.MissingValueException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;
import me.monst.pluginutil.configuration.transform.Transformer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A configuration value, which can be changed at runtime.
 * The configuration value has a name {@code key}, which is given by its parent branch and unique among its siblings.
 * The configuration value also has a value of type {@code T}, which can be changed at runtime, and a default value to
 * fall back on if necessary.
 * The configuration value can be converted to and from a YAML object using a {@link Transformer}.
 * @param <T> the type of the value
 */
public class ConfigurationValue<T> extends ConfigurationNode {

    private final T defaultValue;
    private T value;
    private final Transformer<T> transformer;
    private final Set<T> history = new LinkedHashSet<>();
    
    /**
     * Creates a new configuration value with the given key and default value.
     * @param key the key of the value
     * @param defaultValue the default value
     * @param transformer the transformer used to convert the file data to and from the desired type
     */
    public ConfigurationValue(String key, T defaultValue, Transformer<T> transformer) {
        super(key);
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.transformer = transformer;
        // Add default value as first element in history, even though it is not a change
        // This ensures that the value always has something to tab-complete with
        history.add(defaultValue);
    }
    
    /**
     * Gets the current value of this configuration value.
     * @return the current value
     */
    public final T get() {
        return value;
    }
    
    /**
     * Sets the value of this configuration value, adding the old value to the history.
     * The new value is removed from this history if it is already present.
     * @param newValue the new value
     */
    private void set(T newValue) {
        if (Objects.equals(value, newValue))
            return;
        history.add(value);
        history.remove(newValue);
        value = newValue;
    }
    
    /**
     * Feeds an arbitrary object to this configuration value. The object will be converted to the
     * desired type using the transformer, and the value will be set to the validated result.
     * If the object is null or the transformer throws an exception, the value will be set to the default value.
     * @param object the object to feed the node
     */
    @Override
    protected final void feed(Object object) {
        try {
            transformer.nullCheck(object);
            set(transformer.convert(object));
        } catch (ValueOutOfBoundsException e) {
            set(e.getReplacement());
        } catch (MissingValueException | UnreadableValueException e) {
            set(defaultValue);
        }
    }
    
    /**
     * Parses a user-entered string to a new value, and sets this configuration value.
     * @param input user input to be parsed, null if the value should be reset
     * @throws ArgumentParseException if the input could not be parsed
     */
    public final void feed(String input) throws ArgumentParseException {
        T newValue;
        try {
            transformer.nullCheck(input);
            newValue = transformer.parse(input);
        } catch (MissingValueException e) {
            newValue = defaultValue;
        }
        beforeSet();
        set(newValue);
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
        if (args.size() > 1)
            return Collections.emptyList();
        return history.stream().map(transformer::format).collect(Collectors.toList());
    }
    
    /**
     * Gets the default value of this configuration value as defined in the constructor.
     * @return the default value of this configuration value
     */
    public final T getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Gets the transformer used for converting the data to and from the desired type.
     * @return this configuration value's transformer
     */
    public final Transformer<T> getTransformer() {
        return transformer;
    }
    
    /**
     * Gets the history of this configuration value.
     * @return the history of this configuration value
     */
    public Set<T> getHistory() {
        return history;
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
