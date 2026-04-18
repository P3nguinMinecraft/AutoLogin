package com.autologin;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class Command {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
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
                                    if (Config.password.isBlank()) {
                                        ChatUtils.sendChatMessage("[AutoLogin] You have not set a password yet!");
                                        return 0;
                                    }
                                    ChatUtils.sendChatMessage("[AutoLogin] Your password is: " + Config.password);
                                    return 1;
                                })
                        )
                )
                .then(literal("commands")
                        .then(literal("add")
                                .then(argument("command", StringArgumentType.word())
                                        .executes(context -> {
                                            String command = Config.normalizeCommand(context.getArgument("command", String.class));
                                            if (!Config.isValidCommand(command)) {
                                                ChatUtils.sendChatMessage("[AutoLogin] Invalid command. Use only lowercase letters, numbers, and underscores.");
                                                return 0;
                                            }
                                            if (Config.commands.contains(command)) {
                                                ChatUtils.sendChatMessage("[AutoLogin] Command already exists: " + command);
                                                return 0;
                                            }

                                            Config.commands.add(command);
                                            Config.save();
                                            ChatUtils.sendChatMessage("[AutoLogin] Added command: " + command);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("command", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            for (String key : Config.commands) {
                                                builder.suggest(key);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            String command = Config.normalizeCommand(context.getArgument("command", String.class));
                                            if (!Config.commands.remove(command)) {
                                                ChatUtils.sendChatMessage("[AutoLogin] Command not found: " + command);
                                                return 0;
                                            }

                                            if (Config.commands.isEmpty()) {
                                                Config.commands.add("login");
                                                ChatUtils.sendChatMessage("[AutoLogin] Command list was empty, restored default: login");
                                            }

                                            Config.save();
                                            ChatUtils.sendChatMessage("[AutoLogin] Removed command: " + command);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("list")
                                .executes(context -> {
                                    ChatUtils.sendChatMessage("[AutoLogin] Commands: " + String.join(", ", Config.commands));
                                    return 1;
                                })
                        )
                )
                .then(literal("toggle")
                        .executes(context -> {
                            Config.enabled = !Config.enabled;
                            if (Config.enabled && (Config.password == null || Config.password.isEmpty())) {
                                ChatUtils.sendChatMessage("[AutoLogin] You have not set a password yet!");
                                Config.enabled = false;
                                return 0;
                            }
                            ChatUtils.sendChatMessage("[AutoLogin] Mod is now " + (Config.enabled ? "enabled." : "disabled."));
                            Config.save();
                            return 1;
                        })
                )
        ));
    }
}
