package me.monst.pluginutil.configuration;

/**
 * A node in a configuration tree. This node can be fed an arbitrary object and will attempt to make sense of
 * it and store it in a way that makes sense for the node.
 * Every node has a key which is used to identify it among its siblings.
 */
public abstract class ConfigurationNode {
    
    private final String key;
    
    public ConfigurationNode(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
    
    /**
     * Feeds the given object to this node. The node will attempt to make sense of the object and
     * store it in a way that makes sense for the node. This may not be possible, in which case
     * the node will fall back on a default value.
     * @param object the object to feed the node
     */
    protected abstract void feed(Object object);
    
    /**
     * Gets the value of this node as a YAML object.
     * @return the value of this node as a YAML object
     */
    protected abstract Object getAsYaml();

}
