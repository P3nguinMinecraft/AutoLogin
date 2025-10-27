package com.autologin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("autologin.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean enabled = false;
    public static String password = "";

    public static void save() {
        try {
            Files.deleteIfExists(configFile);

            JsonObject json = new JsonObject();
            json.addProperty("configVersion", 1);
            json.addProperty("enabled", enabled);
            json.addProperty("password", password);

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

            if(!(!json.has("configVersion") || json.get("configVersion").getAsInt() != 1)) {
                if (json.has("enabled"))
                    enabled = json.getAsJsonPrimitive("enabled").getAsBoolean();
                if (json.has("password"))
                    password = json.getAsJsonPrimitive("password").getAsString();
            }

            save();
            AutoLogin.LOGGER.info("Config file loaded successfully.");
        } catch (IOException e) {
            AutoLogin.LOGGER.error("Failed to load config file", e);
        }
    }
}