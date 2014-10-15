package com.battleroid.killboard.commands;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Battleroid on 10/14/2014.
 */
public class KBCommand extends CommandBase {
    public String getCommandName() {
        return "kb";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "kb.commands.usage";
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        } else {
            return null;
        }
    }

    public void processCommand(ICommandSender sender, String[] args) {
        String name = "Console";
        if (sender instanceof EntityPlayer) {
            name = ((EntityPlayer) sender).getDisplayName();
        }
        if (args.length > 0 && args[0].length() > 0) {
            ChatComponentText msg = new ChatComponentText(name + " sent " + StringUtils.join(args, " "));
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(msg);
        } else {
            throw new WrongUsageException("kb.commands.usage", new Object[0]);
        }
    }
}
