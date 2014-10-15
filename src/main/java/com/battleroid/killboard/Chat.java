package com.battleroid.killboard;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

/**
 * Created by Battleroid on 10/14/2014.
 */
public class Chat {
    public enum Color {
        BLACK("\u00A70"),
        DARKBLUE("\u00A71"),
        DARKGREEN("\u00A72"),
        DARKAQUA("\u00A73"),
        DARKRED("\u00A74"),
        DARKPURPLE("\u00A75"),
        GOLD("\u00A76"),
        GRAY("\u00A77"),
        DARKGRAY("\u00A78"),
        BLUE("\u00A79"),
        GREEN("\u00A7a"),
        AQUA("\u00A7b"),
        RED("\u00A7c"),
        LIGHTPURPLE("\u00A7d"),
        YELLOW("\u00A7e"),
        WHITE("\u00A7f"),
        SCRAMBLE("\u00A7k"),
        BOLD("\u00A7l"),
        STRIKE("\u00A7m"),
        ULINE("\u00A7n"),
        ITALIC("\u00A7o"),
        RESET("\u00A7r"),
        LINESEP("\n");

        private final String color;

        private Color(final String color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return color;
        }
    }

    public static void sendMsg(ICommandSender sender, String message) {
        sender.addChatMessage(msgText(message));
    }

    private static IChatComponent msgText(String message) {
        ChatComponentText msg = new ChatComponentText(message);
        return msg;
    }
}
