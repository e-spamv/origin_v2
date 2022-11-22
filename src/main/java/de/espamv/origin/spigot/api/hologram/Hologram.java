package de.claved.origin.spigot.api.hologram;

import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Hologram {

    private final OriginPlayer player;
    private final HologramText text;
    private final Location location;

    private final List<EntityArmorStand> entityList = new ArrayList<>();

    public Hologram(OriginPlayer player, Location location, HologramText text) {
        this.player = player;
        this.text = text;
        this.location = location;
        this.create();
    }

    public void destroy() {
        hide();
        entityList.clear();
    }

    public void show(int duration) {
        show();
        Bukkit.getScheduler().runTaskLater(Origin.getInstance(), this::hide, duration);
    }

    public void show() {
        this.entityList.forEach(entityArmorStand -> {
            PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
            player.sendPacket(packetPlayOutSpawnEntityLiving);
        });
    }

    public void hide() {
        entityList.forEach(entityArmorStand -> {
            PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
            player.sendPacket(packetPlayOutEntityDestroy);
        });
    }

    public void respawn() {
        this.hide();
        Bukkit.getScheduler().runTaskLater(Origin.getInstance(), this::show, 2);
    }

    public void updateText() {
        hide();
        entityList.clear();
        Bukkit.getScheduler().runTaskLater(Origin.getInstance(), () -> {
            this.create();
            this.show();
        }, 2);
    }

    private void create() {
        double distance = 0.25D;

        List<String> list = this.text.text();

        Location location = this.location.clone();
        location.subtract(0, 2, 0);
        location.add(0, (list.size() - 1) * distance, 0);

        list.forEach(text -> {
            EntityArmorStand entityArmorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
            entityArmorStand.setCustomName(text);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setInvisible(true);
            entityArmorStand.setGravity(false);
            entityList.add(entityArmorStand);
            location.subtract(0, distance, 0);
        });
    }

    public interface HologramText {
        List<String> text();
    }
}
