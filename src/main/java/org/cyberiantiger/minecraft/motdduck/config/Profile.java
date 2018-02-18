/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.motdduck.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import org.cyberiantiger.minecraft.motdduck.Main;

/**
 *
 * @author antony
 */
public class Profile {
    private static final PlayerInfo[] EMPTY_PLAYER_LIST = new PlayerInfo[0];
    private static final Comparator<PlayerInfo> PLAYER_INFO_COMPARATOR =
            (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
    private static final Comparator<ServerInfo> SERVER_COMPARATOR =
            (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());

    private enum PlayerListType {
        NONE() {
            @Override
            public PlayerInfo[] getPlayerInfos(Main plugin, Profile profile, List<ProxiedPlayer> players) {
                return EMPTY_PLAYER_LIST;
            }
        },
        PLAYERS() {
            @Override
            public PlayerInfo[] getPlayerInfos(Main plugin, Profile profile, List<ProxiedPlayer> players) {
                PlayerInfo[] result = new PlayerInfo[Math.min(players.size(), profile.maxPlayerList)];
                for (int i = 0; i < result.length; i++) {
                    ProxiedPlayer player = players.remove(RNG.get().nextInt(players.size()));
                    result[i] = new PlayerInfo(player.getName(), player.getUniqueId());
                }
                Arrays.sort(result, PLAYER_INFO_COMPARATOR);
                return result;
            }
        },
        FIXED() {
            @Override
            public PlayerInfo[] getPlayerInfos(Main plugin, Profile profile, List<ProxiedPlayer> players) {
                int playerCount = players.size();
                int maxPlayers = profile.maxPlayers;
                if (profile.playerListFixed == null) {
                    return EMPTY_PLAYER_LIST;
                }
                PlayerInfo[] result = new PlayerInfo[profile.playerListFixed.size()];
                for (int i = 0; i < profile.playerListFixed.size(); i++) {
                    result[i] = new PlayerInfo(String.format(profile.playerListFixed.get(i),playerCount,maxPlayers), "");
                }
                return result;
            }
        },
        NETWORK() {
            @Override
            public PlayerInfo[] getPlayerInfos(Main plugin, Profile profile, List<ProxiedPlayer> players) {
                int playerCount = players.size();
                int maxPlayers = profile.maxPlayers;
                List<PlayerInfo> result = new ArrayList<>();
                if (profile.playerListNetworkHeader != null) {
                    for (String s : profile.playerListNetworkHeader) {
                        result.add(new PlayerInfo(String.format(s, playerCount, maxPlayers), ""));
                    }
                }
                for (ServerInfo info : profile.getPlayerListNetworkServers(plugin)) {
                    int serverPlayerCount = info.getPlayers().size();
                    result.add(new PlayerInfo(String.format(profile.playerListNetworkServerFormat, info.getName(), serverPlayerCount, maxPlayers), ""));
                }
                if (profile.playerListNetworkFooter != null) {
                    for (String s : profile.playerListNetworkFooter) {
                        result.add(new PlayerInfo(String.format(s, playerCount, maxPlayers), ""));
                    }
                }
                return result.toArray(new PlayerInfo[result.size()]);
            }
        };

        protected abstract PlayerInfo[] getPlayerInfos(Main plugin, Profile profile, List<ProxiedPlayer> players);

    }

    private String icon;
    private Map<Integer, DuckProtocol> protocolVersions;
    private List<String> dynamicMotd;
    private List<String> staticMotd;
    private int maxPlayers;
    private List<String> playerListServers;
    private final PlayerListType playerListType = PlayerListType.NONE;
    private final List<String> whitelistUsers = new ArrayList<>();

    private final int maxPlayerList = 10;

    private List<String> playerListFixed;

    private List<String> playerListNetworkHeader;
    private List<String> playerListNetworkServers;
    private List<String> playerListNetworkFooter;
    private final String playerListNetworkServerFormat = "%s (%d/%d)";

    private transient boolean loadedFavicon;
    private transient Favicon favicon;
    private transient boolean loadedPlayerList;
    private transient Set<ServerInfo> playerListServersSet;
    private transient List<ServerInfo> playerListNetworkServersList;

    private static final ThreadLocal<Random> RNG = ThreadLocal.withInitial(Random::new);

    public Favicon getFavicon(Main plugin) {
        synchronized(this) {
            if (!loadedFavicon) {
                if (icon != null) {
                    File faviconFile = new File(plugin.getDataFolder(), icon);
                    if (faviconFile.isFile()) {
                        try {
                            favicon = Favicon.create(ImageIO.read(faviconFile));
                        } catch (IOException ex) {
                            plugin.getLogger().log(Level.WARNING, "Error loading icon file: " + faviconFile, ex);
                        }
                    }
                }
                loadedFavicon = true;
            }
        }
        return favicon;
    }

    public Protocol getProtocol(Main plugin, PendingConnection c) {
        synchronized(this) {
            if (protocolVersions == null) {
                return null;
            } else {
                DuckProtocol result;
                if (protocolVersions.containsKey(c.getVersion())) {
                    result = protocolVersions.get(c.getVersion());
                } else {
                    result = protocolVersions.get(0);
                }
                return result == null ? null : result.asProtocol();
            }
        }
    }

    public String getDynamicMotd(String user) {
        if (dynamicMotd != null && !dynamicMotd.isEmpty())  {
            return String.format(dynamicMotd.get(RNG.get().nextInt(dynamicMotd.size())), user);
        } else {
            return null;
        }
    }

    public String getStaticMotd() {
        if (staticMotd != null && !staticMotd.isEmpty()) {
            return staticMotd.get(RNG.get().nextInt(staticMotd.size()));
        } else {
            return null;
        }
    }
    
    public boolean getWhitelistMode() {
        return false;
    }

    public String getWhitelistMsg() {
        return "You are not whitelisted for this server.";
    }

    public List<String> getWhitelistUsers() {
    	return whitelistUsers;
    }

    public Players getPlayers(Main plugin) {
        ProxyServer proxy = plugin.getProxy();
        synchronized(this) {
            if (!loadedPlayerList) {
                if (playerListServers != null) {
                    playerListServersSet = new HashSet<>(playerListServers.size());
                    for (String server : playerListServers) {
                        ServerInfo serverInfo = proxy.getServerInfo(server);
                        if (serverInfo != null) {
                            playerListServersSet.add(serverInfo);
                        }
                    }
                }
                loadedPlayerList = true;
            }
        }
        List<ProxiedPlayer> result = new LinkedList<>();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            Server server = player.getServer();
            if (server != null) {
                if (playerListServersSet != null) {
                    if(playerListServersSet.contains(server.getInfo())) {
                        result.add(player);
                    }
                } else {
                    result.add(player);
                }
            }
        }
        return new Players(maxPlayers, result.size(), playerListType.getPlayerInfos(plugin, this, result));
    }

    private Iterable<ServerInfo> getPlayerListNetworkServers(Main plugin) {
        synchronized (this) {
            if (playerListNetworkServersList == null) {
                playerListNetworkServersList = new ArrayList<>();
                if (playerListNetworkServers != null) {
                    for (String s : playerListNetworkServers) {
                        ServerInfo info = plugin.getProxy().getServerInfo(s);
                        if (info != null) {
                            playerListNetworkServersList.add(info);
                        }
                    }
                } else {
                    playerListNetworkServersList.addAll(plugin.getProxy().getServers().values());
                    playerListNetworkServersList.sort(SERVER_COMPARATOR);
                }
            }
            return playerListNetworkServersList;
        }
    }
}
