package com.gmail.artemis.the.gr8.playerstats.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class OfflinePlayerHandler {

    private OfflinePlayerHandler() {
    }

    public static boolean isOfflinePlayer(String playerName) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).anyMatch(offlinePlayer -> offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName));
    }

    public static OfflinePlayer getOfflinePlayer(String playerName) {
        OfflinePlayer[] playerList = Bukkit.getOfflinePlayers();
        OfflinePlayer offlinePlayer = null;

        for(OfflinePlayer player : playerList) {
            if(player.getName() != null && player.getName().equalsIgnoreCase(playerName)) {
                offlinePlayer = player;
                break;
            }
        }
        return offlinePlayer;
    }

    public static List<OfflinePlayer> getAllOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer ->
            offlinePlayer.getName() != null && offlinePlayer.hasPlayedBefore()).collect(Collectors.toList());
    }

    public static List<String> getAllOfflinePlayerNames() {
        return getAllOfflinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
    }
}
