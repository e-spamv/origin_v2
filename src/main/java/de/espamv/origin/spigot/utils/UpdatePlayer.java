package de.claved.origin.spigot.utils;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.manager.OriginManager;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Field;

public class UpdatePlayer {

    public static void update(OriginPlayer player, boolean self) {
        if (self) updateSelf(player);
        Bukkit.getScheduler().runTaskLater(Origin.getInstance(), () -> OriginManager.getInstance().getPlayers().stream().filter(players -> players.canSee(player)).forEach(players -> {
            players.hidePlayer(player);
            players.showPlayer(player);
        }), 1);
    }

    private static void updateSelf(OriginPlayer player) {
        PacketPlayOutPlayerInfo removePlayer = buildPlayerInfoPacket(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        PacketPlayOutPlayerInfo addPlayer = buildPlayerInfoPacket(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        PacketPlayOutRespawn respawnPlayer = new PacketPlayOutRespawn();

        player.sendPacket(removePlayer);

        boolean flying = player.isFlying();
        Location location = player.getLocation();

        player.sendPacket(respawnPlayer);
        player.setFlying(flying);
        player.teleport(location);
        player.updateInventory();
        player.sendPacket(addPlayer);
    }

    private static Field setAccessible(Field field) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        return field;
    }

    private static PacketPlayOutPlayerInfo buildPlayerInfoPacket(PacketPlayOutPlayerInfo.EnumPlayerInfoAction enumPlayerInfoAction) {
        try {
            PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo();
            setAccessible(packetPlayOutPlayerInfo.getClass().getDeclaredField("a")).set(packetPlayOutPlayerInfo, enumPlayerInfoAction);
            return packetPlayOutPlayerInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
