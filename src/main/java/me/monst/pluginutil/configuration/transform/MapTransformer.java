package me.monst.pluginutil.configuration.transform;

import me.monst.pluginutil.configuration.exception.ArgumentParseException;
import me.monst.pluginutil.configuration.exception.UnreadableValueException;
import me.monst.pluginutil.configuration.exception.ValueOutOfBoundsException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MapTransformer<K, V> implements Transformer<Map<K, V>> {
    
    private final Supplier<? extends Map<K, V>> mapFactory;
    private final Transformer<K> keyTransformer;
    private final Transformer<V> valueTransformer;
    
    public MapTransformer(Supplier<? extends Map<K, V>> mapFactory,
                          Transformer<K> keyTransformer,
                          Transformer<V> valueTransformer) {
        this.mapFactory = mapFactory;
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
    }
    
    @Override
    public Map<K, V> parse(String input) throws ArgumentParseException {
        Map<K, V> map = mapFactory.get();
        for (String s : input.split("\\s*(,|\\s)\\s*")) {
            String[] split = s.split("[=:]", 2);
            if (split.length != 2)
                throw new ArgumentParseException("'" + s + "' is not a map entry.");
            K key = keyTransformer.parse(split[0]);
            V value = valueTransformer.parse(split[1]);
            map.put(key, value);
        }
        return map;
    }
    
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
    
    @Override
    public Object toYaml(Map<K, V> value) {
        Map<Object, Object> map = new LinkedHashMap<>();
        value.forEach((k, v) -> map.put(keyTransformer.toYaml(k), valueTransformer.toYaml(v)));
        return map;
    }
    
    @Override
    public String format(Map<K, V> value) {
        return value.entrySet().stream()
                .map(e -> keyTransformer.format(e.getKey()) + "=" + valueTransformer.format(e.getValue()))
                .collect(Collectors.joining(", "));
    }
    
}
