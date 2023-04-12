package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.function.Function;

public class MappingTransformer<T, R> implements Transformer<R> {

    private final Transformer<T> transformer;
    private final Function<T, R> forwardMapper;
    private final Function<R, T> reverseMapper;
    
    public MappingTransformer(Transformer<T> transformer, Function<T, R> forwardMapper, Function<R, T> reverseMapper) {
        this.transformer = transformer;
        this.forwardMapper = forwardMapper;
        this.reverseMapper = reverseMapper;
    }
    
    @Override
    public R parse(String input) throws ArgumentParseException {
        return forwardMapper.apply(transformer.parse(input));
    }
    
    @Override
    public R convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        return forwardMapper.apply(transformer.convert(object));
    }
    
    @Override
    public Object toYaml(R value) {
        return transformer.toYaml(reverseMapper.apply(value));
    }
    
    @Override
    public String format(R value) {
        return transformer.format(reverseMapper.apply(value));
    }

}
