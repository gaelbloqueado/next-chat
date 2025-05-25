package com.gaeldev.nextchat; // Paquete base

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class CommandBlockerConfig {

    private final NextChat plugin;
    private File configFile;
    private FileConfiguration config;

    private Map<String, GroupConfigEntry> groupConfigs = new HashMap<>();
    private List<GroupConfigEntry> sortedGroupsByPriority = new ArrayList<>();

    public CommandBlockerConfig(NextChat plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "commands.yml");
        load();
    }

    public void load() {
        if (!configFile.exists()) {
            plugin.saveResource("commands.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultConfigStream = plugin.getResource("commands.yml");
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
            config.setDefaults(defaultConfig);
        }
        parseConfig();
    }

    private void parseConfig() {
        groupConfigs.clear();
        sortedGroupsByPriority.clear();

        ConfigurationSection groupsSection = config.getConfigurationSection("groups");
        if (groupsSection == null) {
            plugin.getLogger().warning("La sección 'groups' no fue encontrada en commands.yml.");
            return;
        }

        for (String groupName : groupsSection.getKeys(false)) {
            ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);
            if (groupSection != null) {
                String denyMessage = groupSection.getString("deny-message", "&cComando no permitido.");
                List<String> commands = groupSection.getStringList("commands");
                String inherits = groupSection.getString("inherits");
                int priority = groupSection.getInt("priority", 0);
                groupConfigs.put(groupName.toLowerCase(), new GroupConfigEntry(groupName.toLowerCase(), priority, commands, denyMessage, inherits));
            }
        }
        resolveInheritance();

        sortedGroupsByPriority = groupConfigs.values().stream()
                .sorted(Comparator.comparingInt(GroupConfigEntry::getPriority).reversed())
                .collect(Collectors.toList());
    }

    private void resolveInheritance() {
        for (GroupConfigEntry group : groupConfigs.values()) {
            Set<String> resolvedCommands = new HashSet<>(group.getRawCommands());
            String currentInherits = group.getInherits();
            Set<String> visited = new HashSet<>();

            while (currentInherits != null && !currentInherits.isEmpty() && !visited.contains(currentInherits.toLowerCase())) {
                visited.add(currentInherits.toLowerCase());
                GroupConfigEntry parentGroup = groupConfigs.get(currentInherits.toLowerCase());
                if (parentGroup != null) {
                    resolvedCommands.addAll(parentGroup.getRawCommands());
                    currentInherits = parentGroup.getInherits();
                } else {
                    plugin.getLogger().warning("Grupo '" + group.getName() + "' intenta heredar de un grupo no existente: '" + currentInherits + "'.");
                    break;
                }
            }
            if (currentInherits != null && visited.contains(currentInherits.toLowerCase())) {
                plugin.getLogger().severe("¡Dependencia circular detectada en la herencia de grupos de commands.yml comenzando desde el grupo: " + currentInherits + "!");
            }
            group.setResolvedCommands(resolvedCommands);
        }

        for (int i = 0; i < groupConfigs.size(); i++) {
            for (GroupConfigEntry group : groupConfigs.values()) {
                Set<String> finalCommands = new HashSet<>(group.getRawCommands());
                String inherits = group.getInherits();
                if (inherits != null && !inherits.isEmpty()) {
                    GroupConfigEntry parent = groupConfigs.get(inherits.toLowerCase());
                    if (parent != null) {
                        finalCommands.addAll(parent.getResolvedCommands());
                    }
                }
                group.setResolvedCommands(finalCommands);
            }
        }
    }

    public GroupConfigEntry getPlayerGroup(Player player) {
        for (GroupConfigEntry groupEntry : sortedGroupsByPriority) {
            if (player.hasPermission("nextchat.group." + groupEntry.getName())) {
                return groupEntry;
            }
        }
        return groupConfigs.getOrDefault("default", null);
    }

    public String getDenyMessageForPlayer(Player player) {
        GroupConfigEntry group = getPlayerGroup(player);
        if (group != null) {
            return group.getDenyMessage();
        }
        GroupConfigEntry defaultGroup = groupConfigs.get("default");
        if (defaultGroup != null && defaultGroup.getDenyMessage() != null) {
            return defaultGroup.getDenyMessage();
        }
        return "&cComando no permitido.";
    }

    public int getGroupCount() {
        return groupConfigs.size();
    }

    public static class GroupConfigEntry {
        private final String name;
        private final int priority;
        private final List<String> rawCommands;
        private Set<String> resolvedCommands;
        private final String denyMessage;
        private final String inherits;

        public GroupConfigEntry(String name, int priority, List<String> commands, String denyMessage, String inherits) {
            this.name = name;
            this.priority = priority;
            this.rawCommands = commands.stream().map(String::toLowerCase).collect(Collectors.toList());
            this.denyMessage = denyMessage;
            this.inherits = inherits;
            this.resolvedCommands = new HashSet<>(this.rawCommands);
        }

        public String getName() { return name; }
        public int getPriority() { return priority; }
        public List<String> getRawCommands() { return Collections.unmodifiableList(rawCommands); }
        public Set<String> getResolvedCommands() { return Collections.unmodifiableSet(resolvedCommands); }
        public void setResolvedCommands(Set<String> resolvedCommands) {
            this.resolvedCommands = new HashSet<>(resolvedCommands.stream().map(String::toLowerCase).collect(Collectors.toSet()));
        }
        public String getDenyMessage() { return denyMessage; }
        public String getInherits() { return inherits; }
    }
}