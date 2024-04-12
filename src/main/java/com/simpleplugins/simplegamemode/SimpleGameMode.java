package com.simpleplugins.simplegamemode;

import com.simpleplugins.simplegamemode.commands.*;
import com.simpleplugins.simplegamemode.config.MessagesConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

@Author("SimplePlugins")
@Plugin(name = "SimpleGameMode", version = "1.0.1")
public class SimpleGameMode extends JavaPlugin {
    private static SimpleGameMode instance;

    @Getter
    private MessagesConfig messagesConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        messagesConfig = new MessagesConfig();
        messagesConfig.reload();

        registerCommands();
    }

    private void registerCommands() {
        final Map<Class<?>, String> map = new HashMap<>();

        map.put(GameModeCommand.class, "gm");
        map.put(SurvivalCommand.class, "survival");
        map.put(CreativeCommand.class, "creative");
        map.put(AdventureCommand.class, "adventure");
        map.put(SpectatorCommand.class, "spectator");

        try {
            final Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);

            final CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());

            for (Class<?> command : map.keySet()) {
                final BukkitCommand bukkitCommand = getCommand(command, map.get(command));
                if (bukkitCommand != null) commandMap.register(bukkitCommand.getName(), bukkitCommand);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Could not register commands on command map");
        }

        getLogger().log(Level.INFO,"registered " + map.size() + " Commands successful");
        map.clear();
    }

    private @Nullable BukkitCommand getCommand(@NotNull Class<?> commandClass, @NotNull String path) {
        final ConfigurationSection section = getConfig().getConfigurationSection("commands." + path);
        if (section == null) return null;

        Constructor<?> constructor = null;
        try {
            constructor = commandClass.getConstructor(String.class, ConfigurationSection.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Could not get Constructor");
        }

        if (constructor == null) {
            getLogger().log(Level.SEVERE, "Command constructor is null");
            return null;
        }

        final String name = section.getString("name");
        if (name == null) return null;

        BukkitCommand command = null;
        try {
            command = (BukkitCommand) constructor.newInstance(name, section);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            getLogger().log(Level.SEVERE, "Could not create a new constructor instance");
        }

        if (command == null) {
            getLogger().log(Level.SEVERE, "Command is null");
            return null;
        }

        if (!section.getBoolean("enabled")) return null;
        command.setAliases(section.getStringList("aliases"));

        return command;
    }

    public static SimpleGameMode getInstance() {
        return instance;
    }
}