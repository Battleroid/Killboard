package com.battleroid.killboard;

import com.battleroid.killboard.Chat.Color;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.sqlite.JDBC;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Battleroid on 10/14/2014.
 */
public class Database {
    private static final Logger log = Logger.getLogger("killboard");
    private Connection conn;

    public enum Top {
        kills, deaths
    }

    class Row implements Comparable<Row> {
        String name;
        int kills;
        int deaths;

        public Row(String name, int kills, int deaths) {
            this.name = name;
            this.kills = kills;
            this.deaths = deaths;
        }

        @Override
        public int compareTo(Row x) {
            return kills < x.kills ? -1 : kills > x.kills ? 1 : 0;
        }
    }

    @EventHandler
    public void create(FMLServerStartingEvent event) {
        try {
            this.connect();
            this.setup();
        } catch (SQLException e) {
            File f = new File("killboard.db");
            if (f.exists() && !f.isDirectory()) {
                log.info("killboard.db already exists.");
            } else {
                log.warning("killboard database could not be created.");
            }
        } catch (ClassNotFoundException e) {
            log.warning("ClassNotFoundException");
        }
    }

    @EventHandler
    public void close(FMLServerStoppingEvent event) {
        try {
            this.conn.close();
            log.info("Closing killboard.db connection.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws SQLException, ClassNotFoundException {
        DriverManager.registerDriver(new JDBC());
        conn = DriverManager.getConnection("jdbc:sqlite:killboard.db");
        log.info("SQLite driver loaded!");
    }

    private void setup() throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("CREATE TABLE killboard (uuid TEXT PRIMARY KEY NOT NULL, name TEXT UNIQUE NOT NULL, kills INTEGER DEFAULT(0), deaths INTEGER DEFAULT(0) )");
        } finally {
            st.close();
        }
    }

    public void statsTop(ICommandSender sender, Top search) throws SQLException {
        String sql = null;
        Statement st = conn.createStatement();
        switch (search) {
            case kills:
                sql = "SELECT name, kills, deaths FROM killboard ORDER BY kills DESC LIMIT 3";
                break;
            case deaths:
                sql = "SELECT name, kills, deaths FROM killboard ORDER BY deaths DESC LIMIT 3";
                break;
            default:
                break;
        }
        ResultSet rs = st.executeQuery(sql);
        List<Row> users = new ArrayList<Row>();
        while (rs.next()) {
            users.add(new Row(rs.getString("name"), rs.getInt("kills"), rs.getInt(("deaths"))));
        }
        for (Row x : users) {
            Chat.sendMsg(sender, String.format("[%s%s%s] K:%d D:%d", Color.GOLD, x.name, Color.WHITE, x.kills, x.deaths));
        }
        if (users.size() == 0) {
            Chat.sendMsg(sender, String.format("No users on killboard."));
        }
    }

    public void statsLookup(ICommandSender sender, String username) throws SQLException {
        String sql = "SELECT kills, deaths FROM killboard WHERE name = ?";
        PreparedStatement prep = conn.prepareStatement(sql);
        prep.setString(1, username);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            int kills = rs.getInt("kills");
            int deaths = rs.getInt("deaths");
            Chat.sendMsg(sender, String.format("[%s%s%s] K:%d D:%d", Color.GOLD, username, Color.WHITE, kills, deaths));
        } else {
            Chat.sendMsg(sender, String.format("No user by the name of '%s' found.", username));
        }
    }

    public void addKB(EntityPlayer player, Boolean killer) {
        String uuid = player.getUniqueID().toString();
        PreparedStatement prep;
        try {
            String sql;
            if (killer) {
                sql = "UPDATE killboard SET kills = kills + 1 WHERE uuid = ?";
            } else {
                sql = "UPDATE killboard SET deaths = deaths + 1 WHERE uuid = ?";
            }
            prep = conn.prepareStatement(sql);
            prep.setString(1, uuid);
            prep.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            addKB(player, false);
            if (event.source.getEntity() instanceof EntityPlayer) {
                EntityPlayer killer = (EntityPlayer) event.source.getEntity();
                addKB(killer, true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerLoggedInEvent event) throws SQLException {
        EntityPlayer player = event.player;
        String uuid = player.getUniqueID().toString();
        String name = player.getDisplayName();
        PreparedStatement prep;
        String sql = "INSERT OR IGNORE INTO killboard (name, uuid) VALUES (?, ?)";
        prep = conn.prepareStatement(sql);
        prep.setString(1, name);
        prep.setString(2, uuid);
        int rowsAffected = prep.executeUpdate();
        if (rowsAffected == 1)
            Chat.sendMsg(player, String.format("%s You have been added to the killboard, %s.", Color.GRAY, name));
    }
}
