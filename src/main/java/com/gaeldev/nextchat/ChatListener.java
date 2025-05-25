package com.gaeldev.nextchat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatListener implements Listener {

    private final NextChat plugin;

    public ChatListener(NextChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (plugin.getChatCooldownSeconds() > 0 && !player.hasPermission("nextchat.bypass.cooldown")) {
            long currentTime = System.currentTimeMillis();
            long lastMessageTime = plugin.getPlayerCooldowns().getOrDefault(playerId, 0L);
            long cooldownMillis = TimeUnit.SECONDS.toMillis(plugin.getChatCooldownSeconds());

            if (currentTime - lastMessageTime < cooldownMillis) {
                long timeLeftMillis = cooldownMillis - (currentTime - lastMessageTime);
                long timeLeftSeconds = (long) Math.ceil(timeLeftMillis / 1000.0);

                String message = plugin.getCooldownMessageConfig().replace("{time}", String.valueOf(timeLeftSeconds));
                player.sendMessage(plugin.colorize(message));
                event.setCancelled(true);
                return;
            }
            plugin.getPlayerCooldowns().put(playerId, currentTime);
        }

        String rawMessage = event.getMessage();
        String playerMessageSegment;

        if (player.hasPermission("nextchat.color")) {
            playerMessageSegment = plugin.translateColors(rawMessage);
        } else {
            playerMessageSegment = rawMessage;
        }

        String formattedChatLine = plugin.formatMessageContent(player, playerMessageSegment);
        event.setFormat(formattedChatLine);
    }
}