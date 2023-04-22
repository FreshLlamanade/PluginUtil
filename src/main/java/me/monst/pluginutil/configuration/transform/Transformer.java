package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.MissingValueException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;
import me.monst.pluginutil.configuration.validation.Bounds;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link Transformer} is a function that converts data to and from a desired type.
 * It can be used to convert data from a YAML file to a Java object, or to convert a Java object to a YAML object.
 * It can also be used to parse user input into a Java object, and format it back into a string for display.
 * @param <T> the type of the data
 */
public interface Transformer<T> {
    
    /**
     * Parses the given input string into an object of type {@code T}.
     * This input can be user input or data from a YAML file.
     * If the input is readable but invalid, it should be automatically corrected.
     * @param input the input string
     * @return the parsed object
     * @throws ArgumentParseException if the input could not be parsed
     */
    T parse(String input) throws ArgumentParseException;
    
    /**
     * Optional method used to convert data from a YAML file to a Java object quicker than {@link #parse(String)}.
     * This can be useful if the YAML data is already in the correct type or a more efficient conversion is possible.
     * By default, this method calls {@link #parse(String)} on the String representation of the object.
     * @param object the YAML data
     * @return the converted object
     * @throws ValueOutOfBoundsException if the value is invalid but could be corrected
     * @throws UnreadableValueException if the value could not be read
     */
    default T convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        return parse(String.valueOf(object));
    }
    
    /**
     * Checks if the given object is null and throws a {@link MissingValueException} if it is.
     * This may be overridden in case a null value is valid.
     * @param object the object to check
     * @throws MissingValueException if the object is null and should not be null
     */
    default void nullCheck(Object object) throws MissingValueException {
        if (object == null)
            throw new MissingValueException();
    }
    
    /**
     * Converts the given object to a YAML object.
     * By default, this method returns the String representation of the object.
     * This should be overridden in cases where the data can be converted to a more efficient YAML representation.
     * @param value the object to convert
     * @return the converted YAML object
     */
    default Object toYaml(T value) {
        return String.valueOf(value);
    }
    
    /**
     * Formats the given object into a string for display.
     * @param value the object to format
     * @return the formatted string
     */
    default String format(T value) {
        return String.valueOf(value);
    }
    
    /**
     * Returns a {@link Transformer} that applies the given bounds to this transformer.
     * @param bounds the bounds to apply
     * @return the bounded transformer
     */
    default Transformer<T> bounded(Bounds<T> bounds) {
        return new BoundedTransformer<>(this, bounds);
    }
    
    /**
     * Returns a {@link Transformer} that collects the values of this transformer into a collection.
     * @param collectionFactory the factory used to create the collection
     * @return the collection transformer
     * @param <C> the type of the collection
     */
    default <C extends Collection<T>> Transformer<C> collect(Supplier<? extends C> collectionFactory) {
        return new CollectionTransformer<>(this, collectionFactory);
    }
    
    /**
     * Returns a {@link Transformer} that wraps the value of this transformer in an {@link Optional}.
     * @return the optional transformer
     */
    default Transformer<Optional<T>> optional() {
        return new OptionalTransformer<>(this);
    }
    
    /**
     * Returns a {@link Transformer} that maps the value of this transformer to another type.
     * In order to support conversion in both directions, the reverse mapper is also required.
     * @param forwardMapper the forward mapper function
     * @param reverseMapper the reverse mapper function
     * @return the mapped transformer
     * @param <R> the type of the mapped value
     */
    default <R> Transformer<R> map(Function<T, R> forwardMapper, Function<R, T> reverseMapper) {
        return new MappingTransformer<>(this, forwardMapper, reverseMapper);
    }
    
}
