package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A transformer that uses two other transformers to construct a map of keys and values.
 * @param <K> The type of the keys in the map.
 * @param <V> The type of the values in the map.
 */
public class MapTransformer<K, V> implements Transformer<Map<K, V>> {
    
    private final Supplier<? extends Map<K, V>> mapFactory;
    private final Transformer<K> keyTransformer;
    private final Transformer<V> valueTransformer;
    
    /**
     * Creates a new map transformer.
     * @param mapFactory A supplier that creates a new map instance.
     * @param keyTransformer The transformer to use for keys.
     * @param valueTransformer The transformer to use for values.
     */
    public MapTransformer(Supplier<? extends Map<K, V>> mapFactory,
                          Transformer<K> keyTransformer,
                          Transformer<V> valueTransformer) {
        this.mapFactory = mapFactory;
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
    }
    
    /**
     * Parses a string into a map of keys and values, splitting the string by commas and spaces to get the individual
     * entries, and splitting each entry by an equals sign or colon to get the key and value.
     * @param input the input string
     * @return the map of keys and values
     * @throws ArgumentParseException if the input string could not be parsed
     */
    @Override
    public Map<K, V> parse(String input) throws ArgumentParseException {
        Map<K, V> map = mapFactory.get();
        for (String s : input.split("\\s*(,|\\s)\\s*")) {
            String[] split = s.split("[=:]", 2);
            if (split.length != 2)
                throw new ArgumentParseException("'" + s + "' is not a map entry."); // TODO: Better error message
            K key = keyTransformer.parse(split[0]);
            V value = valueTransformer.parse(split[1]);
            map.put(key, value);
        }
        return map;
    }
    
    /**
     * Converts the YAML data to a map of keys and values. This expects the YAML data to be a map.
     * @param object the YAML data
     * @return the map of keys and values
     * @throws ValueOutOfBoundsException if a value is out of bounds
     * @throws UnreadableValueException if the YAML data is not a map
     */
    @Override
    public Map<K, V> convert(Object object) throws ValueOutOfBoundsException, UnreadableValueException {
        if (!(object instanceof Map))
            throw new UnreadableValueException();
        boolean problemFound = false;
        Map<K, V> map = mapFactory.get();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
            try {
                K key;
                try {
                    key = keyTransformer.convert(entry.getKey());
                } catch (ValueOutOfBoundsException e) {
                    key = e.getReplacement();
                    problemFound = true;
                }
                V value;
                try {
                    value = valueTransformer.convert(entry.getValue());
                } catch (ValueOutOfBoundsException e) {
                    value = e.getReplacement();
                    problemFound = true;
                }
                map.put(key, value);
            } catch (UnreadableValueException e) {
                problemFound = true;
            }
        }
        if (problemFound)
            throw new ValueOutOfBoundsException(map);
        return map;
    }
    
    /**
     * Converts a map of keys and values to YAML data.
     * @param value the object to convert
     * @return the YAML data
     */
    @Override
    public Object toYaml(Map<K, V> value) {
        Map<Object, Object> map = new LinkedHashMap<>();
        value.forEach((k, v) -> map.put(keyTransformer.toYaml(k), valueTransformer.toYaml(v)));
        return map;
    }
    
    /**
     * Formats a map of keys and values into a string.
     * @param value the object to format
     * @return the formatted string
     */
    @Override
    public String format(Map<K, V> value) {
        return value.entrySet().stream()
                .map(e -> keyTransformer.format(e.getKey()) + "=" + valueTransformer.format(e.getValue()))
                .collect(Collectors.joining(", "));
    }
    
}
