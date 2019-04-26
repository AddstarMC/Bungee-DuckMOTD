package org.cyberiantiger.minecraft.motdduck;

import net.md_5.bungee.api.ProxyServer;
import org.cyberiantiger.minecraft.motdduck.config.Config;

import java.io.File;

/**
 * Created for the Charlton IT Project.
 * Created by benjicharlton on 26/04/2019.
 */
public class TestMain extends Main {
    private File config;
    private ProxyServer server;
    public TestMain(File config, ProxyServer proxyServer) {
        this.config = config;
        this.server = proxyServer;
    }

    @Override
    public ProxyServer getProxy() {
        return server;
    }

    public File getConfigFile() {
        return config;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

}
