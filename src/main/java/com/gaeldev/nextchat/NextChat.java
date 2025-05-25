package com.gaeldev.nextchat;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class NextChat extends JavaPlugin {

    private String pluginPrefix;
    private String defaultChatFormat;
    private int chatCooldownSeconds;
    private String cooldownMessageConfig;
    private String reloadMessageConfig;
    private String noPermissionMessageConfig;
    private String commandUsageMessageConfig;

    private boolean placeholderApiHooked = false;
    private final Map<UUID, Long> playerCooldowns = new HashMap<>();

    private CommandBlockerConfig commandBlockerConfig;
    private LegacyComponentSerializer inputDeserializer;
    private LegacyComponentSerializer outputSerializer;

    private boolean joinQuitMessagesEnabled;
    private String joinMessageFormat;
    private String quitMessageFormat;

    @Override
    public void onEnable() {
        this.inputDeserializer = LegacyComponentSerializer.builder()
                .character('&')
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();

        this.outputSerializer = LegacyComponentSerializer.legacySection();

        saveDefaultConfig();
        this.commandBlockerConfig = new CommandBlockerConfig(this);

        loadMainConfigValues();

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandBlockerListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderApiHooked = true;
        }

        Objects.requireNonNull(getCommand("nextchat")).setExecutor(new NextChatCommand(this));

        String version = this.getDescription().getVersion();
        int gruposCargados = commandBlockerConfig.getGroupCount();
        String pluginPrefixPlain = ChatColor.stripColor(translateColors(this.pluginPrefix));

        getLogger().info("[" + pluginPrefixPlain + "] Activando NextChat v" + version);
        getLogger().info("=============================");
        getLogger().info(pluginPrefixPlain + " v" + version);
        getLogger().info("Developer: @gaeldev");
        getLogger().info("Grupos cargados: " + gruposCargados);
        getLogger().info("=============================");
    }

    @Override
    public void onDisable() {
        playerCooldowns.clear();
        String pluginPrefixPlain = ChatColor.stripColor(translateColors(this.pluginPrefix));
        getLogger().info(pluginPrefixPlain + " ha sido deshabilitado.");
    }

    public void loadMainConfigValues() {
        reloadConfig();
        pluginPrefix = getConfig().getString("plugin-prefix", "&6Next&fChat");
        defaultChatFormat = getConfig().getString("default-chat-format", "&7%player_name%&f: {message}");
        chatCooldownSeconds = getConfig().getInt("chat-cooldown-seconds", 3);

        cooldownMessageConfig = getConfig().getString("messages.cooldown", "{prefix} &cPor favor espera {time} segundos antes de enviar otro mensaje.");
        noPermissionMessageConfig = getConfig().getString("messages.no-permission", "{prefix} &cNo tienes permiso para hacer esto.");
        reloadMessageConfig = getConfig().getString("messages.reload", "{prefix} &aConfiguración recargada.");
        commandUsageMessageConfig = getConfig().getString("messages.command-usage", "{prefix} &7Usa &e/nextchat reload &7para recargar la configuración.");

        joinQuitMessagesEnabled = getConfig().getBoolean("join-quit-messages.enabled", true);
        joinMessageFormat = getConfig().getString("join-quit-messages.join-message", "&e%player_name% se ha unido al servidor.");
        quitMessageFormat = getConfig().getString("join-quit-messages.quit-message", "&e%player_name% ha abandonado el servidor.");
    }

    public void reloadAllConfigs() {
        loadMainConfigValues();
        commandBlockerConfig.load();
    }

    public String getPluginPrefix() {
        return pluginPrefix;
    }

    public String getDefaultChatFormat() {
        return defaultChatFormat;
    }

    public int getChatCooldownSeconds() {
        return chatCooldownSeconds;
    }

    public Map<UUID, Long> getPlayerCooldowns() {
        return playerCooldowns;
    }

    public boolean isPlaceholderApiHooked() {
        return placeholderApiHooked;
    }

    public String getCooldownMessageConfig() {
        return cooldownMessageConfig;
    }

    public String getNoPermissionMessageConfig() {
        return noPermissionMessageConfig;
    }

    public String getReloadMessageConfig() {
        return reloadMessageConfig;
    }

    public String getCommandUsageMessageConfig() {
        return commandUsageMessageConfig;
    }

    public CommandBlockerConfig getCommandBlockerConfig() {
        return commandBlockerConfig;
    }

    public boolean isJoinQuitMessagesEnabled() {
        return joinQuitMessagesEnabled;
    }

    public String getJoinMessageFormat() {
        return joinMessageFormat;
    }

    public String getQuitMessageFormat() {
        return quitMessageFormat;
    }

    public String translateColors(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        Component component = this.inputDeserializer.deserialize(message);
        return this.outputSerializer.serialize(component);
    }

    public String formatMessageContent(Player player, String playerMessageSegment) {
        String chatFormatTemplate = getDefaultChatFormat();
        final String tempMessagePlaceholder = "%%__NEXTCHAT_PLAYER_MSG__%%";
        String formatWithTempPlaceholder = chatFormatTemplate.replace("{message}", tempMessagePlaceholder);

        if (isPlaceholderApiHooked()) {
            formatWithTempPlaceholder = PlaceholderAPI.setPlaceholders(player, formatWithTempPlaceholder);
        } else {
            formatWithTempPlaceholder = formatWithTempPlaceholder.replace("%player_name%", player.getDisplayName());
            formatWithTempPlaceholder = formatWithTempPlaceholder.replace("{player_name}", player.getDisplayName());
        }

        String fullyColoredTemplate = translateColors(formatWithTempPlaceholder);
        String finalMessage = fullyColoredTemplate.replace(tempMessagePlaceholder, playerMessageSegment);

        return finalMessage;
    }

    public String colorize(String textWithPrefixPlaceholder) {
        if (textWithPrefixPlaceholder == null) return "";
        String text = textWithPrefixPlaceholder.replace("{prefix}", getPluginPrefix());
        return translateColors(text);
    }

    public void sendMessage(Player player, String messageWithCodes) {
        player.sendMessage(this.inputDeserializer.deserialize(messageWithCodes));
    }
}