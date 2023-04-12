package me.monst.pluginutil.command;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface Permission {
    
    Permission NONE = sender -> true;
    
    static Permission of(String permission) {
        return sender -> sender.hasPermission(permission);
    }
    
    boolean ownedBy(CommandSender sender);
    
    default boolean notOwnedBy(CommandSender sender) {
        return !ownedBy(sender);
    }
    
    default Permission and(Permission other) {
        return sender -> ownedBy(sender) && other.ownedBy(sender);
    }
    
    default Permission or(Permission other) {
        return sender -> ownedBy(sender) || other.ownedBy(sender);
    }
    
    default Permission negate() {
        return sender -> !ownedBy(sender);
    }

}
