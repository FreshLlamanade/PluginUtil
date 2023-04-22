package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.function.Function;

/**
 * A transformer that maps the values of another transformer.
 * @param <T> The type of the values of the other transformer.
 * @param <R> The type of the values of this transformer.
 */
public class MappingTransformer<T, R> implements Transformer<R> {

    private final Transformer<T> transformer;
    private final Function<T, R> forwardMapper;
    private final Function<R, T> reverseMapper;
    
    public MappingTransformer(Transformer<T> transformer, Function<T, R> forwardMapper, Function<R, T> reverseMapper) {
        this.transformer = transformer;
        this.forwardMapper = forwardMapper;
        this.reverseMapper = reverseMapper;
    }
    
    /**
     * Parses a string into a value using the other transformer, then applies the forward mapper to the result.
     * @param input the input string
     * @return the parsed and mapped value
     * @throws ArgumentParseException if the input could not be parsed
     */
    @Override
    public R parse(String input) throws ArgumentParseException {
        return forwardMapper.apply(transformer.parse(input));
    }
    
    /**
     * Converts the YAML data to a value using the other transformer, then applies the forward mapper to the result.
     * @param object the YAML data
     * @return the converted and mapped value
     * @throws ValueOutOfBoundsException if the value is out of bounds
     * @throws UnreadableValueException if the value could not be read
     */
    @Override
    public R convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        return forwardMapper.apply(transformer.convert(object));
    }
    
    /**
     * Performs a reverse mapping on the value, then converts it to YAML using the other transformer.
     * @param value the object to convert
     * @return the YAML data
     */
    @Override
    public Object toYaml(R value) {
        return transformer.toYaml(reverseMapper.apply(value));
    }
    
    /**
     * Performs a reverse mapping on the value, then formats it using the other transformer.
     * @param value the object to format
     * @return the formatted string
     */
    @Override
    public String format(R value) {
        return transformer.format(reverseMapper.apply(value));
    }

}
