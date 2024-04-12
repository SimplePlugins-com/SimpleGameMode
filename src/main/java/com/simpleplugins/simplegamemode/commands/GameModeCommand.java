package com.simpleplugins.simplegamemode.commands;

import com.simpleplugins.simplegamemode.SimpleGameMode;
import com.simpleplugins.simplegamemode.config.MessagesConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameModeCommand extends BukkitCommand {
    private final ConfigurationSection section;

    public GameModeCommand(@NotNull String name, @NotNull ConfigurationSection section) {
        super(name);

        this.section = section;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        final MessagesConfig messages = SimpleGameMode.getInstance().getMessagesConfig();

        final String permission = section.getString("permission");
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(messages.getMessage("no_permission"));
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender, messages);
            return true;
        }

        GameMode gamemode = null;
        if (args[0].startsWith("c") || args[0].equals("1"))
            gamemode = GameMode.CREATIVE;
        else if (args[0].startsWith("su") || args[0].equals("0"))
            gamemode = GameMode.SURVIVAL;
        else if (args[0].startsWith("a") || args[0].equals("2"))
            gamemode = GameMode.ADVENTURE;
        else if (args[0].startsWith("s") || args[0].equals("3"))
            gamemode = GameMode.SPECTATOR;

        if (gamemode == null) {
            sendUsage(sender, messages);
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sendUsage(sender, messages);
                return true;
            }

            ((Player) sender).setGameMode(gamemode);
            sender.sendMessage(messages.getMessage("gamemode_" + gamemode.name().toLowerCase()));
            return true;
        }

        final String permissionOther = section.getString("permission-other");
        if (permissionOther != null && !sender.hasPermission(permissionOther)) {
            sender.sendMessage(messages.getMessage("no_permission"));
            return true;
        }

        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(messages.getMessage("player_not_found"));
            return true;
        }

        player.setGameMode(gamemode);
        sender.sendMessage(messages.getMessage("gamemode_" + gamemode.name().toLowerCase() + "_other")
                .replace("{Player}", player.getName()));
        return true;
    }

    private void sendUsage(@NotNull CommandSender sender, @NotNull MessagesConfig messages) {
        final String permissionOther = section.getString("permission-other");

        if (permissionOther != null && sender.hasPermission(permissionOther)) {
            sender.sendMessage(messages.getMessage("usage_other"));
        } else {
            sender.sendMessage(messages.getMessage("usage"));
        }
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        final List<String> list = new ArrayList<>();

        if (args.length == 1) {
            final String permission = section.getString("permission");
            if(permission != null && !sender.hasPermission(permission)) return list;

            list.add("0");
            list.add("1");
            list.add("2");
            list.add("3");
        }

        if (args.length == 2) {
            final String permissionOther = section.getString("permission-other");
            if(permissionOther != null && !sender.hasPermission(permissionOther)) return list;

            for (Player all : Bukkit.getOnlinePlayers())
                list.add(all.getName());
        }

        return list;
    }
}