package org.cyberiantiger.minecraft.motdduck;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 26/04/2019.
 */
class TestHelper {
    private static UUID testUUID;
    private static InetSocketAddress address;
    private static ListenerInfo info;
    public static PendingConnection createPendingConnection(final int version){
        testUUID = UUID.randomUUID();
        address = new InetSocketAddress("localhost",22577);
        List<String> serverPriority = new ArrayList<>();
        info = new ListenerInfo(address,"Test MOTD",10,10,serverPriority,true,null,"NETWORK",false,true,22577,true,true);
        return new PendingConnection() {
            @Override
            public String getName() {
                return "TestConnection";
            }

            @Override
            public int getVersion() {
                return version;
            }

            @Override
            public InetSocketAddress getVirtualHost() {
                return address;
            }

            @Override
            public ListenerInfo getListener() {
                return info;
            }

            @SuppressWarnings("deprecation")
            @Override
            public String getUUID() {
                return testUUID.toString();
            }

            @Override
            public UUID getUniqueId() {
                return testUUID;
            }

            @Override
            public void setUniqueId(UUID uuid) {
                testUUID = uuid;
            }

            @Override
            public boolean isOnlineMode() {
                return false;
            }

            @Override
            public void setOnlineMode(boolean onlineMode) {
                throw new UnsupportedOperationException("NOT YET SUPPORTED");
            }

            @Override
            public boolean isLegacy() {
                return false;
            }

            @SuppressWarnings("deprecation")
            @Override
            public InetSocketAddress getAddress() {
                return address;
            }

            @Override
            public SocketAddress getSocketAddress() {
                return null;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void disconnect(String reason) {
                throw new UnsupportedOperationException("NOT YET SUPPORTED");

            }

            @Override
            public void disconnect(BaseComponent... reason) {
                throw new UnsupportedOperationException("NOT YET SUPPORTED");

            }

            @Override
            public void disconnect(BaseComponent reason) {
                throw new UnsupportedOperationException("NOT YET SUPPORTED");
            }

            @Override
            public boolean isConnected() {
                return true;
            }

            @Override
            public Unsafe unsafe() {
                return null;
            }
        };
    }
    static ServerPing createServerPing(String name, int version){
        ServerPing ping = new ServerPing();
        ping.setVersion(new ServerPing.Protocol(name,version));
        return ping;
    }

    static ProxyServer createTestProxy(){
        //noinspection deprecation
        return new ProxyServer() {
            @Override
            public String getName() {
                return "Test";
            }

            @Override
            public String getVersion() {
                return "1.14";
            }

            @Override
            public String getTranslation(String name, Object... args) {
                return null;
            }

            @Override
            public Logger getLogger() {
                return Logger.getLogger("Test");
            }

            @Override
            public Collection<ProxiedPlayer> getPlayers() {
                return Collections.emptyList();
            }

            @Override
            public ProxiedPlayer getPlayer(String name) {
                return null;
            }

            @Override
            public ProxiedPlayer getPlayer(UUID uuid) {
                return null;
            }

            @Override
            public Map<String, ServerInfo> getServers() {
                return null;
            }

            @Override
            public ServerInfo getServerInfo(String name) {
                return null;
            }

            @Override
            public PluginManager getPluginManager() {
                return null;
            }

            @Override
            public ConfigurationAdapter getConfigurationAdapter() {
                return null;
            }

            @Override
            public void setConfigurationAdapter(ConfigurationAdapter adapter) {

            }

            @Override
            public ReconnectHandler getReconnectHandler() {
                return null;
            }

            @Override
            public void setReconnectHandler(ReconnectHandler handler) {

            }

            @Override
            public void stop() {

            }

            @Override
            public void stop(String reason) {

            }

            @Override
            public void registerChannel(String channel) {

            }

            @Override
            public void unregisterChannel(String channel) {

            }

            @Override
            public Collection<String> getChannels() {
                return null;
            }

            @SuppressWarnings("deprecation")
            @Override
            public String getGameVersion() {
                return null;
            }

            @SuppressWarnings("deprecation")
            @Override
            public int getProtocolVersion() {
                return 0;
            }

            @Override
            public ServerInfo constructServerInfo(String name, InetSocketAddress address, String motd, boolean restricted) {
                return null;
            }

            @Override
            public ServerInfo constructServerInfo(String s, SocketAddress socketAddress, String s1, boolean b) {
                return null;
            }

            @Override
            public CommandSender getConsole() {
                return null;
            }

            @Override
            public File getPluginsFolder() {
                return null;
            }

            @Override
            public TaskScheduler getScheduler() {
                return null;
            }

            @Override
            public int getOnlineCount() {
                return 0;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void broadcast(String message) {
                System.out.println(message);

            }

            @Override
            public void broadcast(BaseComponent... message) {
                for(BaseComponent component:message) {
                    System.out.println(component.toLegacyText());
                }

            }

            @Override
            public void broadcast(BaseComponent message) {
                System.out.println(message.toLegacyText());
            }

            @Override
            public Collection<String> getDisabledCommands() {
                return Collections.emptyList();
            }

            @SuppressWarnings("deprecation")
            @Override
            public ProxyConfig getConfig() {
                return null;
            }

            @Override
            public Collection<ProxiedPlayer> matchPlayer(String match) {
                return Collections.emptyList();
            }

            @Override
            public Title createTitle() {
                return null;
            }
        };
    }
}
