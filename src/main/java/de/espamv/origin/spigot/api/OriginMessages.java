package de.claved.origin.spigot.api;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.events.*;
import de.claved.origin.spigot.api.manager.NotifyManager;
import de.claved.origin.spigot.api.manager.OriginManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class OriginMessages {

    public static class PluginMessageReceivedListener implements PluginMessageListener {

        @Override
        public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
            if (channel.equals("origin")) {
                ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(bytes);
                String subChannel = byteArrayDataInput.readUTF();
                UUID uuid;
                OriginPlayer originPlayer;

                switch (subChannel) {
                    case "updateCoins":
                        uuid = UUID.fromString(byteArrayDataInput.readUTF());
                        originPlayer = OriginManager.getInstance().getPlayer(uuid);

                        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
                            originPlayer.updateCoins();
                            CoinsUpdateEvent coinsUpdateEvent = new CoinsUpdateEvent(originPlayer);
                            Bukkit.getPluginManager().callEvent(coinsUpdateEvent);
                        });
                        break;
                    case "updateLanguage":
                        uuid = UUID.fromString(byteArrayDataInput.readUTF());
                        originPlayer = OriginManager.getInstance().getPlayer(uuid);

                        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
                            originPlayer.updateLanguage();
                            LanguageUpdateEvent languageUpdateEvent = new LanguageUpdateEvent(originPlayer);
                            Bukkit.getPluginManager().callEvent(languageUpdateEvent);
                        });
                        break;
                    case "updateServer":
                        uuid = UUID.fromString(byteArrayDataInput.readUTF());
                        originPlayer = OriginManager.getInstance().getPlayer(uuid);

                        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), originPlayer::updateServer);
                        break;
                    case "updateRank":
                        uuid = UUID.fromString(byteArrayDataInput.readUTF());
                        originPlayer = OriginManager.getInstance().getPlayer(uuid);

                        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
                            originPlayer.updatePoints();
                            PermissionUpdateEvent permissionUpdateEvent = new PermissionUpdateEvent(originPlayer);
                            Bukkit.getPluginManager().callEvent(permissionUpdateEvent);
                        });
                        break;
                    case "updatePoints":
                        uuid = UUID.fromString(byteArrayDataInput.readUTF());
                        originPlayer = OriginManager.getInstance().getPlayer(uuid);

                        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
                            originPlayer.updatePoints();
                            PointsUpdateEvent pointsUpdateEvent = new PointsUpdateEvent(originPlayer);
                            Bukkit.getPluginManager().callEvent(pointsUpdateEvent);
                        });
                        break;
                    case "updateOnlineTime":
                        if (!OriginManager.getInstance().getPlayers().isEmpty()) {
                            Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> OriginManager.getInstance().getPlayers().forEach(players -> {
                                players.updateOnlineTime();
                                OnlineTimeUpdateEvent onlineTimeUpdateEvent = new OnlineTimeUpdateEvent(players);
                                Bukkit.getPluginManager().callEvent(onlineTimeUpdateEvent);
                            }));
                        }
                        break;
                    case "updateNetworkData":
                        String identifier = byteArrayDataInput.readUTF();
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(byteArrayDataInput.readUTF());

                        OriginManager.getInstance().getPlayers().forEach(players -> {
                            NetworkDataUpdateEvent networkDataUpdateEvent = new NetworkDataUpdateEvent(players, identifier, jsonObject);
                            Bukkit.getPluginManager().callEvent(networkDataUpdateEvent);
                        });
                        break;
                    case "updatePlayerCount":
                        OriginManager.getInstance().getPlayers().forEach(players -> {
                            OnlinePlayersUpdateEvent onlinePlayersUpdateEvent = new OnlinePlayersUpdateEvent(players);
                            Bukkit.getPluginManager().callEvent(onlinePlayersUpdateEvent);
                        });
                        break;
                    case "notify":
                        uuid = UUID.fromString(byteArrayDataInput.readUTF());
                        originPlayer = OriginManager.getInstance().getPlayer(uuid);
                        NotifyManager.getInstance().openGUI(originPlayer);
                        break;
                }
            }
        }
    }
}
