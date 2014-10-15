package com.battleroid.killboard.commands;

import com.battleroid.killboard.Database.Top;
import com.battleroid.killboard.Killboard;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.sql.SQLException;

/**
 * Created by Battleroid on 10/14/2014.
 */
public class KBTop extends KBCommand {
    public String getCommandName() {
        return "kbtop";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/kbtop <kills|deaths>";
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            Top search = Top.valueOf(args[0].toLowerCase());
            try {
                Killboard.db.statsTop(sender, search);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args.length == 0) {
            Top search = Top.kills;
            try {
                Killboard.db.statsTop(sender, search);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
        }
    }
}
