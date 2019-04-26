/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.motdduck;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.logging.Level;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import org.cyberiantiger.minecraft.motdduck.config.Config;
import org.cyberiantiger.minecraft.motdduck.config.Profile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;

/**
 *
 * @author antony
 */
public class Main extends Plugin implements Listener {
    private static final String CONFIG = "config.yml";

    private Config config;

    @SuppressWarnings("WeakerAccess")
    protected File getConfigFile() {
        return new File(getDataFolder(), CONFIG);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void saveDefaultConfig() {
        File data = getDataFolder();
        if (!data.exists()) {
            //noinspection ResultOfMethodCallIgnored
            data.mkdir();
        }
        File config = getConfigFile();
        if (!config.exists()) {
            try {
                try (FileOutputStream out = new FileOutputStream(config)) {
                    InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG);
                    if(stream != null) {
                        ByteStreams.copy(stream, out);
                    }
                }
            } catch (IOException ex) {
                getLogger().log(Level.SEVERE, "Could not create config", ex);
                throw new IllegalStateException();
            }
        }
    }

    public void loadConfig() {
        try {
            Yaml configLoader = new Yaml(new CustomClassLoaderConstructor(Config.class, getClass().getClassLoader()));
            configLoader.setBeanAccess(BeanAccess.FIELD);
            config = configLoader.loadAs(new FileReader(getConfigFile()), Config.class);
        } catch (IOException | YAMLException ex) {
            getLogger().log(Level.SEVERE, "Error loading configuration", ex);
        }
    }


    @Override
    public void onLoad() {
        saveDefaultConfig();
        loadConfig();
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new DuckMOTDCommand(this));
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onProxyPing(ProxyPingEvent e) {
        PendingConnection c = e.getConnection();
        ListenerInfo l = c.getListener();
        if (config != null) {
            Profile profile = config.findProfile(this, c, l);
            if (profile != null) {
                ServerPing response = e.getResponse();
                Favicon icon = profile.getFavicon(this);
                Protocol protocol = profile.getProtocol(this, c);
                String motd;
                motd = profile.getStaticMotd();
                Players players = profile.getPlayers(this);
                if (icon != null) {
                    response.setFavicon(icon);
                }
                if (protocol != null) {
                    response.setVersion(protocol);
                    motd = null;
                    response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(ChatColor.DARK_RED+profile.getVersionLowMessage())));
                }
                if (motd != null) {
                    BaseComponent text = new TextComponent(TextComponent.fromLegacyText(motd));
                    response.setDescriptionComponent(text);
                }
                if (players != null) {
                    response.setPlayers(players);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerConnect(PreLoginEvent e) {
        PendingConnection c = e.getConnection();
        ListenerInfo l = c.getListener();
        if (config != null) {
            Profile profile = config.findProfile(this, c, l);
            String name = e.getConnection().getName();
            if (profile != null) {
                if(profile.getWhitelistMode() && profile.getWhitelistUsers() != null) {
                    if (!profile.getWhitelistUsers().contains(name)) {
                        String kickmsg = ChatColor.translateAlternateColorCodes('&', profile.getWhitelistMsg());
                        System.out.println(name + " (" + e.getConnection().getAddress().getAddress() + ") disconnected: " + kickmsg);
                        e.setCancelled(true);
                        e.setCancelReason(TextComponent.fromLegacyText(kickmsg));
                    }
                }else{
                    if(profile.getMinProtocolVersion()>0) {
                        Protocol min = profile.getProtocol(this, c);
                        if(c.getVersion()<min.getProtocol()){
                            e.setCancelReason(TextComponent.fromLegacyText(ChatColor.DARK_RED+profile.getVersionLowMessage()));
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}