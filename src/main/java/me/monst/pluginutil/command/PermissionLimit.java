package me.monst.pluginutil.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface PermissionLimit extends Permission {
    
    @Override
    default boolean ownedBy(CommandSender sender) {
        return getPermissionLimitLong(sender).isPresent();
    }
    
    default boolean isLimitReached(CommandSender sender, int value) {
        return getPermissionLimitInt(sender).map(limit -> value >= limit).orElse(true);
    }
    
    default boolean isLimitReached(CommandSender sender, long value) {
        return getPermissionLimitLong(sender).map(limit -> value >= limit).orElse(true);
    }
    
    default boolean isWithinLimit(CommandSender sender, int value) {
        return getPermissionLimitInt(sender).map(limit -> value <= limit).orElse(false);
    }
    
    default boolean isWithinLimit(CommandSender sender, long value) {
        return getPermissionLimitLong(sender).map(limit -> value <= limit).orElse(false);
    }
    
    default boolean hasLimit(CommandSender sender) {
        return getPermissionLimitLong(sender).isPresent();
    }
    
    Optional<Integer> getPermissionLimitInt(CommandSender sender);
    
    Optional<Long> getPermissionLimitLong(CommandSender sender);
    
    static PermissionLimit of(String basePermission) {
        String base = basePermission.endsWith(".") ? basePermission : basePermission + ".";
        return new PermissionLimit() {
    
            @Override
            public Optional<Integer> getPermissionLimitInt(CommandSender sender) {
                Optional<Integer> limit = Optional.empty();
                for (String ending : getPermissionEndings(sender)) {
                    if (ending.equals("*"))
                        return Optional.of(Integer.MAX_VALUE);
                    try {
                        int newLimit = Integer.parseInt(ending);
                        if (newLimit < 0)
                            continue;
                        if (!limit.isPresent() || newLimit > limit.get())
                            limit = Optional.of(newLimit);
                    } catch (NumberFormatException ignored) {
                        // Someone has a permission like "plugin.permission.abc"
                    }
                }
                return limit;
            }
    
            @Override
            public Optional<Long> getPermissionLimitLong(CommandSender sender) {
                Optional<Long> limit = Optional.empty();
                for (String ending : getPermissionEndings(sender)) {
                    if (ending.equals("*"))
                        return Optional.of(Long.MAX_VALUE);
                    try {
                        long newLimit = Long.parseLong(ending);
                        if (newLimit < 0)
                            continue;
                        if (!limit.isPresent() || newLimit > limit.get())
                            limit = Optional.of(newLimit);
                    } catch (NumberFormatException ignored) {
                        // Someone has a permission like "plugin.permission.abc"
                    }
                }
                return limit;
            }
            
            private Set<String> getPermissionEndings(CommandSender sender) {
                return sender.getEffectivePermissions().stream()
                        .filter(PermissionAttachmentInfo::getValue)
                        .map(PermissionAttachmentInfo::getPermission)
                        .map(perm -> perm.split(base))
                        .filter(split -> split.length > 1)
                        .map(split -> split[1])
                        .collect(Collectors.toSet());
            }
        };
    }

}
