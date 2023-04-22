package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.Optional;

/**
 * A transformer that wraps another transformer and makes it optional.
 * @param <T> The type of underlying transformer.
 */
public class OptionalTransformer<T> implements Transformer<Optional<T>> {
    
    private final Transformer<T> transformer;
    
    public OptionalTransformer(Transformer<T> transformer) {
        this.transformer = transformer;
    }
    
    /**
     * Parses a string into a value using the other transformer, then wraps it in an {@link Optional}.
     * If the input is "null", the result will be {@link Optional#empty()}.
     * @param input the input string
     * @return the parsed value
     * @throws ArgumentParseException if the input could not be parsed
     */
    @Override
    public Optional<T> parse(String input) throws ArgumentParseException {
        if (input.equalsIgnoreCase("null"))
            return Optional.empty();
        return Optional.of(transformer.parse(input));
    }
    
    /**
     * Converts the YAML data to a value using the other transformer, then wraps it in an {@link Optional}.
     * If the input is {@code null}, the result will be {@link Optional#empty()}.
     * @param object the YAML data
     * @return the converted value
     * @throws ValueOutOfBoundsException if the value is out of bounds
     * @throws UnreadableValueException if the value could not be read
     */
    @Override
    public Optional<T> convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        if (object == null)
            return Optional.empty();
        return Optional.of(transformer.convert(object));
    }
    
    /**
     * Does nothing.
     * @param object the object to check
     */
    @Override
    public void nullCheck(Object object) {
        // do nothing
    }
    
    /**
     * Returns {@code null} if the value is empty, otherwise converts it to YAML using the other transformer.
     * @param value the object to convert
     * @return the YAML data
     */
    @Override
    public Object toYaml(Optional<T> value) {
        return value.map(transformer::toYaml).orElse(null);
    }
    
    /**
     * Returns "null" if the value is empty, otherwise formats it using the other transformer.
     * @param value the object to format
     * @return the formatted string
     */
    @Override
    public String format(Optional<T> value) {
        return value.map(transformer::format).orElse("null");
    }
    
}
