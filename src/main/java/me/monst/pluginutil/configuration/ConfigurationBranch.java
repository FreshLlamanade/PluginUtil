package me.monst.pluginutil.configuration;

import java.util.*;

/**
 * A node in the configuration tree that can have children.
 * Each child node is identified by its key.
 * A branch expects to be fed a Map<String, Object> and will feed its children with the values
 * of the map that correspond to their keys.
 */
public abstract class ConfigurationBranch extends ConfigurationNode {
    
    protected final Map<String, ConfigurationNode> children;
    
    /**
     * Creates a new branch with the given key.
     * @param key the key of the branch
     */
    public ConfigurationBranch(String key) {
        super(key);
        this.children = new LinkedHashMap<>();
    }
    
    /**
     * Adds a child node to this branch.
     * @param child the child node to add
     * @return the child node that was added
     * @param <Node> the type of the child node
     */
    public <Node extends ConfigurationNode> Node addChild(Node child) {
        children.put(child.getKey(), child);
        return child;
    }
    
    /**
     * Gets the child node with the given key, or null if no such child exists.
     * @param key the key of the child
     * @return the child node with the given key, or null if no such child exists
     */
    public ConfigurationNode getChild(String key) {
        return children.get(key);
    }
    
    /**
     * Gets a map of all children of this branch.
     * @return a map of all children of this branch
     */
    public Map<String, ConfigurationNode> getChildren() {
        return children;
    }
    
    /**
     * Descend the configuration tree from this branch to find a child node with the given path.
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
    
    /**
     * Feeds the given arbitrary object to this branch. A branch expects to be fed a Map<String, Object>
     * and will feed its children with the values of the map that correspond to their keys.
     * @param object the object to feed the branch
     */
    @Override
    protected void feed(Object object) {
        Map<?, ?> map = object instanceof Map ? (Map<?, ?>) object : Collections.emptyMap();
        children.forEach((key, node) -> node.feed(map.get(key)));
    }
    
    /**
     * Gets the value of this branch as a YAML object, specifically a Map<String, Object>.
     * @return the value of this branch as a YAML object
     */
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
