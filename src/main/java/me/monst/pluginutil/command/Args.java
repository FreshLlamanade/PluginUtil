package me.monst.pluginutil.command;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Args implements Iterable<String> {

    private final LinkedList<String> list;
    
    private Args(List<String> list) {
        this.list = new LinkedList<>(list);
        this.list.pollFirst(); // First argument is always the parent command name, we do not need it
    }
    
    public Args(String[] args) {
        this(Arrays.asList(args));
    }
    
    public int size() {
        return list.size();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public boolean contains(String search) {
        return list.contains(search);
    }
    
    public boolean containsIgnoreCase(String search) {
        for (String arg : list) {
            if (arg.equalsIgnoreCase(search)) {
                return true;
            }
        }
        return false;
    }
    
    public String first() {
        return list.getFirst();
    }
    
    public String last() {
        return list.getLast();
    }
    
    public String get(int index) {
        return list.get(index);
    }
    
    public Args descend() {
        return new Args(list); // Create the Args for a sub-command (this will remove the first element)
    }
    
    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }
    
}
