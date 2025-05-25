package com.gaeldev.nextchat; // Paquete base

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// Importar CommandBlockerConfig desde el paquete base
// Ya no es com.gaeldev.nextchat.config.CommandBlockerConfig
// Sino que CommandBlockerConfig está en el mismo paquete com.gaeldev.nextchat
// por lo que técnicamente no se necesitaría un import explícito si están en el mismo paquete,
// pero es buena práctica mantenerlo o el compilador lo resolverá.
// Para claridad, no se pone un import si la clase está en el mismo paquete.
// Sin embargo, el constructor usa "config.getPlayerGroup(player)" que viene de CommandBlockerConfig.
// Por lo tanto, la variable 'config' debe ser de tipo CommandBlockerConfig.

public class CommandBlockerListener implements Listener {

    private final NextChat plugin;
    private final CommandBlockerConfig config; // Esta es la instancia de tu clase de configuración

    public CommandBlockerListener(NextChat plugin) {
        this.plugin = plugin;
        this.config = plugin.getCommandBlockerConfig(); // Obtener la instancia desde el plugin principal
    }

    private boolean isCommandAllowed(Player player, String command) {
        if (player.hasPermission("nextchat.tabcomplete.bypass")) {
            return true;
        }

        String baseCommand = command.startsWith("/") ? command.substring(1) : command;
        baseCommand = baseCommand.split(" ")[0].toLowerCase();

        if (player.hasPermission("nextchat.tabcomplete." + baseCommand)) {
            return true;
        }

        CommandBlockerConfig.GroupConfigEntry group = config.getPlayerGroup(player);
        if (group != null) {
            return group.getResolvedCommands().contains(baseCommand);
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (!isCommandAllowed(player, command)) {
            event.setCancelled(true);
            String denyMessage = config.getDenyMessageForPlayer(player);
            player.sendMessage(plugin.colorize(denyMessage));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nextchat.tabcomplete.bypass")) {
            return;
        }

        Set<String> groupAllowedCommands = new HashSet<>();
        CommandBlockerConfig.GroupConfigEntry group = config.getPlayerGroup(player);
        if (group != null) {
            groupAllowedCommands.addAll(group.getResolvedCommands());
        }

        Collection<String> commandsToSend = event.getCommands();
        Iterator<String> iterator = commandsToSend.iterator();

        while (iterator.hasNext()) {
            String commandLabel = iterator.next().toLowerCase();
            if (!groupAllowedCommands.contains(commandLabel) && !player.hasPermission("nextchat.tabcomplete." + commandLabel)) {
                iterator.remove();
            }
        }
    }
}