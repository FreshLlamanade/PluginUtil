package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.Optional;

public class OptionalTransformer<T> implements Transformer<Optional<T>> {
    
    private final Transformer<T> transformer;
    
    public OptionalTransformer(Transformer<T> transformer) {
        this.transformer = transformer;
    }
    
    @Override
    public Optional<T> parse(String input) throws ArgumentParseException {
        if (input.equalsIgnoreCase("null"))
            return Optional.empty();
        return Optional.of(transformer.parse(input));
    }
    
    @Override
    public Optional<T> convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        if (object == null)
            return Optional.empty();
        return Optional.of(transformer.convert(object));
    }
    
    @Override
    public void nullCheck(Object object) {
        // do nothing
    }
    
    @Override
    public Object toYaml(Optional<T> value) {
        return value.map(transformer::toYaml).orElse(null);
    }
    
    @Override
    public String format(Optional<T> value) {
        return value.map(transformer::format).orElse("null");
    }
    
}
