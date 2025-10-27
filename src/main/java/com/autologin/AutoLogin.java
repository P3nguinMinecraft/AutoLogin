package com.autologin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AutoLogin implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("AutoLogin");

    private static volatile boolean checked = false;
    @Override
    public void onInitializeClient() {
        Config.load();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
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
            if (dispatcher.getRoot().getChild("login") != null) {
                handler.sendChatCommand("login " + Config.password);
            }
            else if (dispatcher.getRoot().getChild("l") != null){
                handler.sendChatCommand("l " + Config.password);
            }
            else return;
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("autologin")
                .then(literal("password")
                    .then(literal("set")
                        .then(argument("password", StringArgumentType.string())
                            .executes(context -> {
                                Config.password = context.getArgument("password", String.class);
                                ChatUtils.sendChatMessage("[AutoLogin] Password set as: " + Config.password);
                                Config.save();
                                return 1;
                            })
                        )
                    )
                    .then(literal("get")
                        .executes(context -> {
                            ChatUtils.sendChatMessage("[AutoLogin] Your password is: " + Config.password);
                            return 1;
                        })
                    )
                )
                .then(literal("toggle")
                    .executes(context -> {
                        Config.enabled = !Config.enabled;
                        if (Config.enabled && (Config.password == null || Config.password.isEmpty())){
                            ChatUtils.sendChatMessage("[AutoLogin] You have not set a password yet!");
                            Config.enabled = false;
                            return 0;
                        }
                        ChatUtils.sendChatMessage("[AutoLogin] Mod is now " + (Config.enabled ? "enabled." : "disabled."));
                        Config.save();
                        return 1;
                    })
                )
            );
        });

        LOGGER.info("AutoLogin loaded!");
    }
}