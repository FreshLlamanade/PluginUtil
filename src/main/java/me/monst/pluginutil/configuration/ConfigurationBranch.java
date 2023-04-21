package me.monst.pluginutil.configuration;

import java.util.*;

public abstract class ConfigurationBranch extends ConfigurationNode {
    
    protected final Map<String, ConfigurationNode> children;
    
    public ConfigurationBranch(String key) {
        super(key);
        this.children = new LinkedHashMap<>();
    }
    
    public <T extends ConfigurationNode> T addChild(T child) {
        children.put(child.getKey(), child);
        return child;
    }
    
    public ConfigurationNode getChild(String key) {
        return children.get(key);
    }
    
    public Map<String, ConfigurationNode> getChildren() {
        return children;
    }
    
    /**
     * Descend the configuration tree from this node to find a child node with the given path.
     * The path is a list of keys that will be used to descend the tree. If the path is empty,
     * this node will be returned. The first encountered leaf node will be returned and the remaining keys
     * in the path will not be consumed from the iterator. If the path leads to a non-existent node,
     * the last encountered node will be returned and the remaining keys in the path will not be consumed.
     * and any remaining keys in the path will not be consumed from the iterator.
     * @param path the path to descend
     * @return the deepest node that was found for the given path
     */
    public ConfigurationNode deepSearch(ListIterator<String> path) {
        if (!path.hasNext())
            return this;
        ConfigurationNode child = getChild(path.next());
        if (child == null)
            return this;
        if (child instanceof ConfigurationBranch)
            return ((ConfigurationBranch) child).deepSearch(path);
        return child;
    }
    
    @Override
    protected void feed(Object object) {
        Map<?, ?> map = object instanceof Map ? (Map<?, ?>) object : Collections.emptyMap();
        children.forEach((key, node) -> node.feed(map.get(key)));
    }
    
    @Override
    protected Object getAsYaml() {
        Map<String, Object> data = new LinkedHashMap<>();
        children.forEach((key, node) -> data.put(key, node.getAsYaml()));
        return data;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationBranch that = (ConfigurationBranch) o;
        return children.equals(that.children);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getKey(), children);
    }
    
}
