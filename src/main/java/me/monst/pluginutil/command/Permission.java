package me.monst.pluginutil.command;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface Permission {
    
    Permission NONE = sender -> true;
    
    Permission OP = CommandSender::isOp;

    boolean ownedBy(CommandSender sender);
    
    default boolean notOwnedBy(CommandSender sender) {
        return !ownedBy(sender);
    }
    
    static Permission of(String permission) {
        return sender -> sender.hasPermission(permission);
    }
    
    static Permission of(String permission, boolean op) {
        return sender -> sender.hasPermission(permission) && op == sender.isOp();
    }
    
    default Permission and(Permission other) {
        return sender -> ownedBy(sender) && other.ownedBy(sender);
    }
    
    default Permission or(Permission other) {
        return sender -> ownedBy(sender) || other.ownedBy(sender);
    }

}
