package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A transformer that uses another transformer to construct a collection of values.
 * @param <T> The type of the values in the collection.
 * @param <C> The type of the collection.
 */
public class CollectionTransformer<T, C extends Collection<T>> implements Transformer<C> {

    private final Transformer<T> transformer;
    private final Supplier<? extends C> collectionFactory;
    
    public CollectionTransformer(Transformer<T> transformer, Supplier<? extends C> collectionFactory) {
        this.transformer = transformer;
        this.collectionFactory = collectionFactory;
    }
    
    /**
     * Parses a string into a collection of values, splitting the string by commas and spaces to get the individual values.
     * @param input the input string
     * @return the collection of values
     * @throws ArgumentParseException if the input string could not be parsed
     */
    @Override
    public C parse(String input) throws ArgumentParseException {
        C collection = collectionFactory.get();
        for (String s : input.split("\\s*(,|\\s)\\s*"))
            collection.add(transformer.parse(s));
        return collection;
    }
    
    /**
     * Converts a YAML object into a collection of values. This expects the YAML object to be a list of values.
     * If any of the values could not be converted, a {@link ValueOutOfBoundsException} is thrown with the collection
     * of values that could be converted.
     * If any values are out of bounds, they are replaced with the closest valid value, and a
     * {@link ValueOutOfBoundsException} is thrown with the collection of validated values.
     * @param object the YAML data
     * @return the collection of values
     * @throws ValueOutOfBoundsException if any values were unable to be converted or were out of bounds
     * @throws UnreadableValueException if the YAML data could not be read
     */
    @Override
    public C convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        if (!(object instanceof List))
            throw new UnreadableValueException();
        boolean problemFound = false;
        C collection = collectionFactory.get();
        for (Object element : (List<?>) object) {
            try {
                T value = transformer.convert(element);
                if (collection.add(value))
                    continue; // Value was added successfully, continue to next element
            } catch (ValueOutOfBoundsException e) {
                collection.add(e.getReplacement());
            } catch (UnreadableValueException ignored) {}
            problemFound = true; // If we get here, something went wrong
        }
        if (problemFound) // If anything went wrong, throw an exception that the value could not exactly be converted
            throw new ValueOutOfBoundsException(collection);
        return collection;
    }
    
    /**
     * Converts a collection of values into a YAML object. This stores the values as a list in YAML.
     * The type of the values in the list is determined by the underlying transformer.
     * @param collection the object to convert
     * @return the YAML object
     */
    @Override
    public Object toYaml(C collection) {
        return collection.stream().map(transformer::toYaml).collect(Collectors.toList()); // Store as a list in YAML
    }
    
    /**
     * Formats a collection of values into a string. This joins the values with commas and spaces.
     * The format of the values is determined by the underlying transformer.
     * @param collection the object to format
     * @return the formatted string
     */
    @Override
    public String format(C collection) {
        return collection.stream().map(transformer::format).collect(Collectors.joining(", "));
    }

}
