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

import java.util.Random;

import me.kylehunady.Helper;
import me.kylehunady.VillagerEssentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import io.github.bananapuncher714.nbteditor.NBTEditor;

public class ZombieLootHandler implements Listener {
    FileConfiguration config = VillagerEssentials.getPlugin(VillagerEssentials.class).getConfig();
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!(config.getBoolean("enable-easy-zombie-containment"))) return;

        Entity entity = event.getEntity();

        // Check if entity is a zombie variant
        if (!(Helper.isZombieType(entity.getType()))) return;

        Random rand = new Random();
        // generate number 0 or 1 (50% chance)
        // origin is inclusive, bound is exclusive
        boolean canPickUp = rand.nextInt(0,2) == 1;
        
        NBTEditor.set(entity,canPickUp,"CanPickUpLoot");
    }
}