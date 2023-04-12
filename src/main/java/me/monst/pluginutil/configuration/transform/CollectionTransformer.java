package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CollectionTransformer<T, C extends Collection<T>> implements Transformer<C> {

    private final Transformer<T> transformer;
    private final Supplier<? extends C> collectionFactory;
    
    public CollectionTransformer(Transformer<T> transformer, Supplier<? extends C> collectionFactory) {
        this.transformer = transformer;
        this.collectionFactory = collectionFactory;
    }
    
    @Override
    public C parse(String input) throws ArgumentParseException {
        C collection = collectionFactory.get();
        for (String s : input.split("\\s*(,|\\s)\\s*"))
            collection.add(transformer.parse(s));
        return collection;
    }
    
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
    
    @Override
    public Object toYaml(C collection) {
        return collection.stream().map(transformer::toYaml).collect(Collectors.toList()); // Store as a list in YAML
    }
    
    @Override
    public String format(C collection) {
        return collection.stream().map(transformer::format).collect(Collectors.joining(", "));
    }

}
