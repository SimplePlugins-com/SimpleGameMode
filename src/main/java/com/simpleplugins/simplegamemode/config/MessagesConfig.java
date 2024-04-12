package com.simpleplugins.simplegamemode.config;

import com.google.gson.GsonBuilder;
import com.simpleplugins.simplegamemode.SimpleGameMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MessagesConfig {
    private final Map<String,Object> values = new HashMap<>();

    private final Path path;

    public MessagesConfig() {
        path = SimpleGameMode.getInstance().getDataFolder().toPath().resolve("messages.json");
    }

    public String getMessage(String path) {
        return ((String) values.get(path)).replace("{Prefix}",(String) values.get("prefix"));
    }

    public void reload() {
        if(Files.notExists(path))
            SimpleGameMode.getInstance().saveResource(path.getFileName().toString(),false);

        try {
            values.putAll(new GsonBuilder().setPrettyPrinting().create().fromJson(Files.newBufferedReader(path), values.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}