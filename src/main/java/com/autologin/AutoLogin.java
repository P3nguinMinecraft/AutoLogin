package com.autologin;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoLogin implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("AutoLogin");

    private static volatile boolean checked = false;
    @Override
    public void onInitializeClient() {
        Config.load();
        Command.init();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            LOGGER.info("AutoLogin registered join server");
            checked = false;
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (checked) return;
            if (!Config.enabled) return;
            if (Config.password == null || Config.password.isEmpty()) return;
            ClientPlayNetworkHandler handler = client.getNetworkHandler();
            if (handler == null) return;
            CommandDispatcher<ClientCommandSource> dispatcher = handler.getCommandDispatcher();
            if (dispatcher.getRoot().getChildren().isEmpty()) return;
            checked = true;
            for (String command : Config.commands) {
                if (dispatcher.getRoot().getChild(command) != null) {
                    handler.sendChatCommand(command + " " + Config.password);
                    return;
                }
            }
        });

        LOGGER.info("AutoLogin loaded!");
    }
}