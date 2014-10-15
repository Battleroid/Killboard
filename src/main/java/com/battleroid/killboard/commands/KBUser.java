package com.battleroid.killboard.commands;

import com.battleroid.killboard.Killboard;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.sql.SQLException;

/**
 * Created by Battleroid on 10/14/2014.
 */
public class KBUser extends KBCommand {
    public String getCommandUsage(ICommandSender sender) {
        return "/kb <username>";
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0 && args[0].length() > 0) {
            String username = args[0];
            try {
                Killboard.db.statsLookup(sender, username);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args.length == 0) {
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                try {
                    Killboard.db.statsLookup(sender, player.getDisplayName());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
