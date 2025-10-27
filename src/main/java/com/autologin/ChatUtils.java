package com.autologin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtils {
    public static void sendChatMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.inGameHud.getChatHud().addMessage(Text.literal(message));
        }
    }
}
