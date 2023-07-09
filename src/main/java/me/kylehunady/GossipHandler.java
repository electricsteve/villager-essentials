
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

import java.util.Arrays;

import me.kylehunady.VillagerEssentials;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.github.bananapuncher714.nbteditor.NBTEditor.NBTCompound;

// START DEBUGGING IMPORTS
// import java.util.Random;
// import org.bukkit.Bukkit;
// import org.bukkit.event.entity.EntityDamageByEntityEvent;
// END DEBUGGING IMPORTS

public class GossipHandler implements Listener {
    FileConfiguration config = VillagerEssentials.getPlugin(VillagerEssentials.class).getConfig();

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(config.getBoolean("enable-global-discounts"))) return;
        // check if player is right-clicking a villager or zombie villager
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager || entity instanceof ZombieVillager)) return;

        // check if the villager has a Gossips property to parse
        if (!(NBTEditor.contains(entity, "Gossips", 0))) return;

        // the following native MC command (cheats enabled) will grab the Gossips data
        // of the nearest villager
        // /data get entity @e[type=villager, sort=nearest, limit=1] Gossips

        // a villager's "Gossips" property may look like this:
        // [{Target:[I;xxxx,xxxx,xxxx,xxxx],Type:"major_positive",Value:20},
        // {Target:[I;yyyy,yyyy,yyyy,yyyy],Type:"minor_negative",Value:50},
        // {Target:[I;xxxx,xxxx,xxxx,xxxx],Type:"minor_negative",Value:25}]

        // Target is an integer array (the "I;" is for show) representing a player's
        // unique user ID (UUID)
        // Type represents the gossip for that player (major_positive, minor_positive,
        // major_negative, minor_negative, trade)
        // Value represents the weight of that gossip

        // Note: Gossips property is a list. Each element is a compound tag containing
        // Target, Type, and Value
        
        Player player = event.getPlayer();

        String[] gossipTypes = { "minor_positive", "major_positive" };
        for (String type : gossipTypes) {
            addPlayerToGossip(player, entity, type);
        }
    }

    private void addPlayerToGossip(Player player, Entity entity, String gossipType) {
        int maxValue = 0;
        // index is needed to change the player's existing entry in Gossips property
        int playerIndex = -1;
        int playerValue = 0;

        NBTCompound gossips = NBTEditor.getNBTCompound(entity, "Gossips");
        int size = NBTEditor.getSize(gossips);
        int[] playerId = NBTEditor.getIntArray(player, "UUID");
        
        for (int i = 0; i < size; i++) {
            // grab the ith element in Gossips and check its Type
            if (NBTEditor.getString(gossips, i, "Type").equals(gossipType)) {
                int val = NBTEditor.getInt(gossips, i, "Value");
                maxValue = (val > maxValue) ? val : maxValue;

                // determine if the player already has a Gossips entry and save index
                if (Arrays.equals(playerId, NBTEditor.getIntArray(gossips, i, "Target"))) {
                    playerIndex = i;
                    playerValue = val;
                }
            }

        }
        if (maxValue <= 0) return; // if no positive gossip exists, we don't need to update the villager data
        if (playerValue == maxValue) return; // if player has max value, we don't need to update either

        NBTCompound newGossipEntry = NBTEditor.getEmptyNBTCompound();
        newGossipEntry.set(playerId, "Target");
        newGossipEntry.set(gossipType, "Type");
        newGossipEntry.set(maxValue, "Value");

        if (playerIndex >= 0) {
            // this function can be a little backwards to read
            // take our villager data and save the new entry to Gossips[index]
            NBTEditor.set(entity, newGossipEntry, "Gossips", playerIndex);
        } else {
            NBTEditor.set(entity, newGossipEntry, "Gossips", NBTEditor.NEW_ELEMENT);
        }
    }

    // EVERYTHING BELOW IS FOR DEBUGGING
    // @EventHandler
    // public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    //     if (!(event.getEntity() instanceof Villager))
    //         return;
    //     if (!(event.getDamager() instanceof Player))
    //         return;

    //     Villager entity = (Villager) event.getEntity();
    //     // check if the villager has a Gossips property to parse
    //     if (!(NBTEditor.contains(entity, "Gossips", 0)))
    //         return;
    //     NBTCompound tag = NBTEditor.getEmptyNBTCompound();
    //     // int[] playerId = { -1848488881, -494907661, -1780731098, 665058548 };
    //     Random rand = new Random();
    //     int value = rand.nextInt(0, 100);
    //     tag.set("major_positive", "Type");
    //     tag.set(value, "Value");
    //     tag.set(new int[] { 1, 1, 1, 1 }, "Target");
    //     NBTEditor.set(entity, tag, "Gossips", 0); // 0 means added to list at index 0
    //     tag.set("minor_positive", "Type");
    //     NBTEditor.set(entity, tag, "Gossips", 0);
    // }

    // private void printVal(String key, String val) {
    //     Bukkit.broadcastMessage("========================");
    //     Bukkit.broadcastMessage(key + ":" + val);
    // }

    // private void print(String message) {
    //     Bukkit.broadcastMessage(message);
    // }
}