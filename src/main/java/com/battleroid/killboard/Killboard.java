package com.battleroid.killboard;

import com.battleroid.killboard.commands.KBTop;
import com.battleroid.killboard.commands.KBUser;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.logging.Logger;

/**
 * Created by Battleroid on 10/14/2014.
 */
@Mod(modid = Killboard.MODID, version = Killboard.VERSION, acceptableRemoteVersions = "*")
public class Killboard {
    public static final String MODID = "killboard";
    public static final String VERSION = "1.0";

    public static Logger log;
    public static Database db = new Database();

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        FMLCommonHandler.instance().bus().register(db);
        MinecraftForge.EVENT_BUS.register(db);

        event.registerServerCommand(new KBUser());
        event.registerServerCommand(new KBTop());

        db.create(event);
    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {
        db.close(event);
    }
}