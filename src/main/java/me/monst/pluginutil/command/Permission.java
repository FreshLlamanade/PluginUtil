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
    
    static Permission any(Permission... permissions) {
        return sender -> {
            for (Permission permission : permissions) {
                if (permission.ownedBy(sender)) {
                    return true;
                }
            }
            return false;
        };
    }
    
    static Permission all(Permission... permissions) {
        return sender -> {
            for (Permission permission : permissions) {
                if (permission.notOwnedBy(sender)) {
                    return false;
                }
            }
            return true;
        };
    }
    
    default Permission and(Permission other) {
        if (this == NONE) return other;
        if (other == NONE) return this;
        return sender -> ownedBy(sender) && other.ownedBy(sender);
    }
    
    default Permission or(Permission other) {
        if (this == NONE) return NONE;
        if (other == NONE) return NONE;
        return sender -> ownedBy(sender) || other.ownedBy(sender);
    }
    
    default Permission negate() {
        return sender -> !ownedBy(sender);
    }

}
