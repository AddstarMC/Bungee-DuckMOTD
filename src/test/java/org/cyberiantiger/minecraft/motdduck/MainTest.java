/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cyberiantiger.minecraft.motdduck;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.cyberiantiger.minecraft.motdduck.config.Config;
import org.cyberiantiger.minecraft.motdduck.config.DuckProtocol;
import org.cyberiantiger.minecraft.motdduck.config.Profile;
import org.junit.*;

import static org.junit.Assert.*;

import org.junit.experimental.theories.suppliers.TestedOn;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;

/**
 *
 * @author antony
 */
public class MainTest {
    
    public MainTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private static final String PROTOCOL_YAML = "name: 1.7.10\nversion: 5";

    @Test
    public void testDuckProtocol() {
        Yaml configLoader = new Yaml(new CustomClassLoaderConstructor(DuckProtocol.class, getClass().getClassLoader()));
        configLoader.setBeanAccess(BeanAccess.FIELD);
        DuckProtocol proto = configLoader.loadAs(new StringReader(PROTOCOL_YAML), DuckProtocol.class);
        ServerPing.Protocol bungeeProto = proto.asProtocol();
        assertEquals(5, bungeeProto.getProtocol());
        assertEquals("1.7.10", bungeeProto.getName());
    }
    @Test
    public void testProxyPingEvent(){
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testConfig.yml").getFile());
        PendingConnection connection = TestHelper.createPendingConnection(4);
        ProxyPingEvent event = new ProxyPingEvent(connection, TestHelper.createServerPing("1.7.2",4), (result, error) -> {});
        Main main = new TestMain(file,TestHelper.createTestProxy());
        main.loadConfig();
        assertEquals(4,event.getResponse().getVersion().getProtocol());
        main.onProxyPing(event);
        assertEquals(5,event.getResponse().getVersion().getProtocol());
        assertEquals("§4Please update your client to 1.7.4",event.getResponse().getDescriptionComponent().toLegacyText());
        PendingConnection connection2 = TestHelper.createPendingConnection(447);
        ProxyPingEvent event2 = new ProxyPingEvent(connection2, TestHelper.createServerPing("1.14",447), (result, error) -> {});
        main.onProxyPing(event2);
        assertEquals(447,event2.getResponse().getVersion().getProtocol());
        assertEquals("§fTest Static Motd",event2.getResponse().getDescriptionComponent().toLegacyText());
    }


}