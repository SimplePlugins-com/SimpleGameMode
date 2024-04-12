package com.simpleplugins.simplegamemode.commands;

import com.simpleplugins.simplegamemode.SimpleGameMode;
import com.simpleplugins.simplegamemode.config.MessagesConfig;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SurvivalCommand extends BukkitCommand {
    private final ConfigurationSection section;

    public SurvivalCommand(@NotNull String name, @NotNull ConfigurationSection section) {
        super(name);

        this.section = section;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        final MessagesConfig config = SimpleGameMode.getInstance().getMessagesConfig();

        if(!(sender instanceof Player)) {
            sender.sendMessage(config.getMessage("no_console"));
            return true;
        }

        final String permission = section.getString("permission");
        if(permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(config.getMessage("no_permission"));
            return true;
        }

        ((Player) sender).setGameMode(GameMode.SURVIVAL);
        sender.sendMessage(config.getMessage("gamemode_survival"));
        return true;
    }
}