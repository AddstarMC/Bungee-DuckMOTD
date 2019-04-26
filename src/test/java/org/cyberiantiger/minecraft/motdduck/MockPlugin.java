package org.cyberiantiger.minecraft.motdduck;

import net.md_5.bungee.api.ProxyServer;

import java.io.File;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 26/04/2019.
 */
public class MockPlugin extends Main {
    private final File config;
    private final ProxyServer server;
    MockPlugin(File config, ProxyServer proxyServer) {
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
    public void onEnable() {
    }

}
