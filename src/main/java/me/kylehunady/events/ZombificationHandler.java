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

package me.kylehunady.events;

import me.kylehunady.Helper;
import me.kylehunady.VillagerEssentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ZombificationHandler implements Listener {
    FileConfiguration config = VillagerEssentials.getPlugin(VillagerEssentials.class).getConfig();

    /** @noinspection unused */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(config.getBoolean("enable-guaranteed-zombification"))) return;

        Entity deadEntity = event.getEntity();

        // Check if dead entity is a villager
        if (!(deadEntity instanceof Villager)) return;
        Villager villagerEnt = (Villager) deadEntity;

        EntityDamageEvent deathEvent = villagerEnt.getLastDamageCause();

        // Check if death was caused by another entity
        if (!(deathEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
        Entity killerEnt = ((EntityDamageByEntityEvent) deathEvent).getDamager();

        // Check if killer is a zombie variant
        if (!(Helper.isZombieType(killerEnt.getType()))) return;

        villagerEnt.zombify();
    }
}