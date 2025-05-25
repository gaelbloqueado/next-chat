package com.gaeldev.nextchat; // Paquete base

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final NextChat plugin;

    public PlayerConnectionListener(NextChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isJoinQuitMessagesEnabled()) {
            if (plugin.getJoinMessageFormat() == null || plugin.getJoinMessageFormat().isEmpty()) {
                event.setJoinMessage(null);
            }
            return;
        }

        Player player = event.getPlayer();
        String joinFormat = plugin.getJoinMessageFormat();

        if (joinFormat == null || joinFormat.isEmpty()) {
            event.setJoinMessage(null);
            return;
        }

        String message = joinFormat;
        if (plugin.isPlaceholderApiHooked()) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        } else {
            message = message.replace("%player_name%", player.getName());
        }

        event.setJoinMessage(plugin.translateColors(message));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.isJoinQuitMessagesEnabled()) {
            if (plugin.getQuitMessageFormat() == null || plugin.getQuitMessageFormat().isEmpty()) {
                event.setQuitMessage(null);
            }
            return;
        }

        Player player = event.getPlayer();
        String quitFormat = plugin.getQuitMessageFormat();

        if (quitFormat == null || quitFormat.isEmpty()) {
            event.setQuitMessage(null);
            return;
        }

        String message = quitFormat;
        if (plugin.isPlaceholderApiHooked()) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        } else {
            message = message.replace("%player_name%", player.getName());
        }
        event.setQuitMessage(plugin.translateColors(message));
    }
}