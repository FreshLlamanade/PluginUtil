package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.MissingValueException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;
import me.monst.pluginutil.configuration.validation.Bounds;

public class BoundedTransformer<T> implements Transformer<T> {
    
    private final Transformer<T> transformer;
    private final Bounds<T> bounds;
    
    public BoundedTransformer(Transformer<T> transformer, Bounds<T> bounds) {
        this.transformer = transformer;
        this.bounds = bounds;
    }
    
    @Override
    public T parse(String input) throws ArgumentParseException {
        T value = transformer.parse(input);
        return bounds.replace(value);
    }
    
    @Override
    public T convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        T value;
        try {
            value = transformer.convert(object);
        } catch (ValueOutOfBoundsException e) {
            bounds.validate(e.getReplacement()); // Might throw a ValueOutOfBoundsException with an even better replacement
            throw e; // Otherwise, rethrow the original exception
        }
        bounds.validate(value);
        return value;
    }
    
    @Override
    public void nullCheck(Object object) throws MissingValueException {
        transformer.nullCheck(object);
    }
    
    @Override
    public Object toYaml(T value) {
        return transformer.toYaml(value);
    }
    
    @Override
    public String format(T value) {
        return transformer.format(value);
    }
    
}
