/*
 * MIT License
 *
 * Original Copyright (c) 2022 Kyle Hunady
 *
 * Copyright (c) 2023 ElectricSteve
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package me.kylehunady;

import me.kylehunady.bStats.Metrics;
import me.kylehunady.events.GossipHandler;
import me.kylehunady.events.ZombieLootHandler;
import me.kylehunady.events.ZombificationHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public final class VillagerEssentials extends JavaPlugin {
    private static VillagerEssentials plugin;
    public FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        LogInfo("VillagerEssentials is loading...");
        plugin = this;

        // Register config
        LogInfo("Loading config.");
        config.addDefault("enable-guaranteed-zombification", true);
        config.addDefault("enable-global-discounts", true);
        config.addDefault("enable-easy-zombie-containment", true);
        config.addDefault("enable-bstats", true);
        config.addDefault("enable-plugin", true);
        saveConfig();
        LogInfo("Config loaded.");

        // Check if plugin is enabled
        if (!(config.getBoolean("enable-plugin"))) {
            LogInfo("Plugin is disabled in config. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register event listeners
        LogInfo("Registering listeners.");
        getServer().getPluginManager().registerEvents(new GossipHandler(), this);
        getServer().getPluginManager().registerEvents(new ZombieLootHandler(), this);
        getServer().getPluginManager().registerEvents(new ZombificationHandler(), this);
        LogInfo("Listeners registered.");

        // Check if stats should be sent to bStats
        if (config.getBoolean("enable-bstats")) {
            // Register bStats metrics
            int pluginId = 	19039;
            Metrics metrics = new Metrics(this, pluginId);

            // Add custom charts
            metrics.addCustomChart(new Metrics.SimplePie("guaranteed_zombification", () -> {
                return config.getBoolean("enable-guaranteed-zombification") ? "Enabled" : "Disabled";
            }));
            metrics.addCustomChart(new Metrics.SimplePie("global_discounts", () -> {
                return config.getBoolean("enable-global-discounts") ? "Enabled" : "Disabled";
            }));
            metrics.addCustomChart(new Metrics.SimplePie("easy_zombie_containment", () -> {
                return config.getBoolean("enable-easy-zombie-containment") ? "Enabled" : "Disabled";
            }));
            LogInfo("Stats will be sent to bStats.");
        } else {
            // Don't send stats to bStats
            LogInfo("Stats will not be sent to bStats.");
        }

        // Keep at end of method.
        LogInfo("VillagerEssentials enabled!");
    }

    @Override
    public void onDisable() {
        LogInfo("VillagerEssentials disabled!");
    }

    private void LogInfo(String msg) {
        plugin.getLogger().log(Level.INFO, msg);
    }
}