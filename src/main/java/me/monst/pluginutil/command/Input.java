package me.monst.pluginutil.command;

import me.monst.pluginutil.command.exception.CommandExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.UnaryOperator;

public class Input {
    
    private Input() {}
    
    public static Argument.Mapper<String, Player, CommandExecutionException> toPlayer(
            UnaryOperator<String> errorFormatter) {
        return name -> {
            Player player = Bukkit.getPlayer(name);
            if (player == null)
                Command.fail(errorFormatter.apply(name));
            return player;
        };
    }
    
    @Deprecated
    public static Argument.Mapper<String, OfflinePlayer, CommandExecutionException> toOfflinePlayer(
            UnaryOperator<String> errorFormatter) {
        return name -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (!player.hasPlayedBefore())
                Command.fail(errorFormatter.apply(name));
            return player;
        };
    }
    
    public static Argument.Mapper<String, Integer, CommandExecutionException> toInteger(
            UnaryOperator<String> errorFormatter) {
        return string -> {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                throw Command.fail(errorFormatter.apply(string));
            }
        };
    }
    
    public static Argument.Mapper<String, Double, CommandExecutionException> toDouble(
            UnaryOperator<String> errorFormatter) {
        return string -> {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw Command.fail(errorFormatter.apply(string));
            }
        };
    }
    
    public static Argument.Mapper<String, Integer, CommandExecutionException> toCoordinate(
            int relativeTo, UnaryOperator<String> errorFormatter) {
        return string -> {
            try {
                if (!string.startsWith("~"))
                    return Integer.parseInt(string);
                if (string.length() == 1)
                    return relativeTo;
                return relativeTo + Integer.parseInt(string.substring(1));
            } catch (NumberFormatException e) {
                throw Command.fail(errorFormatter.apply(string));
            }
        };
    }
    
}
