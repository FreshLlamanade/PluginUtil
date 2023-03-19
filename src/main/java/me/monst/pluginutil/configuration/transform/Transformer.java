package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.MissingValueException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;
import me.monst.pluginutil.configuration.validation.Bounds;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public interface Transformer<T> {
    
    T parse(String input) throws ArgumentParseException;
    
    default T convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        return parse(String.valueOf(object));
    }
    
    default void nullCheck(Object object) throws MissingValueException {
        if (object == null)
            throw new MissingValueException();
    }
    
    default Object toYaml(T value) {
        return value;
    }
    
    default String format(T value) {
        return String.valueOf(value);
    }
    
    default Transformer<T> bounded(Bounds<T> bounds) {
        return new BoundedTransformer<>(this, bounds);
    }
    
    default <C extends Collection<T>> Transformer<C> collect(Supplier<? extends C> collectionFactory) {
        return new CollectionTransformer<>(this, collectionFactory);
    }
    
    default Transformer<Optional<T>> optional() {
        return new OptionalTransformer<>(this);
    }
    
}
