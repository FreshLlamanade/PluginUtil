package me.monst.pluginutil.configuration;

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
    
    protected abstract Object getAsYaml();

}
