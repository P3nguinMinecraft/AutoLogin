package com.autologin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Config {
    public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("autologin.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final int CONFIG_VERSION = 2;

    public static boolean enabled;
    public static String password;
    public static ArrayList<String> commands = new ArrayList<>();

    static {
        enabled = false;
        password = "";
        commands.add("login");
        commands.add("l");
    }

    public static String normalizeCommand(String command) {
        if (command == null) {
            return "";
        }
        return command.trim().toLowerCase();
    }

    public static boolean isValidCommand(String command) {
        return command.matches("[a-z0-9_]+$");
    }

    public static void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("configVersion", CONFIG_VERSION);
            json.addProperty("enabled", enabled);
            json.addProperty("password", password);

            JsonArray commandsArray = new JsonArray();
            for (String command : commands) {
                commandsArray.add(command);
            }
            json.add("commands", commandsArray);

            Files.writeString(configFile, gson.toJson(json));
        } catch (IOException e) {
            AutoLogin.LOGGER.error("Failed to save config file", e);
        }
    }

    public static void load() {
        try {
            if(!Files.exists(configFile)) {
                Files.createFile(configFile);
                Files.writeString(configFile, "{}");
            }
            JsonObject json = gson.fromJson(Files.readString(configFile), JsonObject.class);

            if (json != null && json.has("configVersion") && json.get("configVersion").getAsInt() == CONFIG_VERSION) {
                if (json.has("enabled")) {
                    enabled = json.getAsJsonPrimitive("enabled").getAsBoolean();
                }
                if (json.has("password")) {
                    password = json.getAsJsonPrimitive("password").getAsString();
                }
                if (json.has("commands") && json.get("commands").isJsonArray()) {
                    ArrayList<String> loadedCommands = new ArrayList<>();
                    for (JsonElement element : json.getAsJsonArray("commands")) {
                        if (!element.isJsonPrimitive()) {
                            continue;
                        }
                        String command = normalizeCommand(element.getAsString());
                        if (isValidCommand(command) && !loadedCommands.contains(command)) {
                            loadedCommands.add(command);
                        }
                    }
                    if (!loadedCommands.isEmpty()) {
                        commands.clear();
                        commands.addAll(loadedCommands);
                    }
                }
            }

            save();
            AutoLogin.LOGGER.info("Config file loaded successfully.");
        } catch (IOException e) {
            AutoLogin.LOGGER.error("Failed to load config file", e);
        }
    }
}