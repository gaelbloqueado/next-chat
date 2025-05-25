package com.gaeldev.nextchat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NextChatCommand implements CommandExecutor, TabCompleter {

    private final NextChat plugin;

    public NextChatCommand(NextChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("nextchat.reload")) {
                    plugin.reloadAllConfigs(); // Updated to reload all configs
                    sender.sendMessage(plugin.colorize(plugin.getReloadMessageConfig()));
                } else {
                    sender.sendMessage(plugin.colorize(plugin.getNoPermissionMessageConfig()));
                }
                return true;
            }
        }
        sender.sendMessage(plugin.colorize(plugin.getCommandUsageMessageConfig().replace("<command>", label)));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("nextchat.reload")) {
                completions.add("reload");
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}