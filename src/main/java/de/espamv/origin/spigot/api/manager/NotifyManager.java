package de.claved.origin.spigot.api.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.OriginPlayer;
import de.claved.origin.spigot.api.inventory.InventoryBuilder;
import de.claved.origin.utils.session.Session;
import de.claved.origin.utils.session.query.QueryStatement;
import de.claved.origin.utils.session.query.UpdateStatement;
import de.claved.origin.spigot.api.inventory.ItemStackBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class NotifyManager implements Listener {

    @Getter
    private static NotifyManager instance;

    @Getter
    private final String prefix = "§8┃ §9Notify §8× §r";

    private final QueryStatement getJsonObject;
    private final UpdateStatement setJsonObject;

    public NotifyManager() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, Origin.getInstance());
        Session session = Origin.getInstance().getSession();

        getJsonObject = session.prepareQueryStatement("SELECT object FROM notifies WHERE uuid = ?");
        setJsonObject = session.prepareUpdateStatement("UPDATE notifies SET object = ? WHERE uuid = ?");
    }

    public void disable() {
        instance = null;
    }

    public JsonObject getObject(UUID uuid) {
        try {
            ResultSet resultSet = getJsonObject.execute(uuid);
            if (resultSet.next()) {
                try {
                    return (JsonObject) new JsonParser().parse(resultSet.getString("object"));
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }

    public void setObject(UUID uuid, JsonObject object) {
        try {
            setJsonObject.execute(object, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void openGUI(OriginPlayer player) {
        InventoryBuilder inventory = new InventoryBuilder(4, "§8Notify");

        inventory.fill(new ItemStackBuilder(Material.STAINED_GLASS_PANE).setData((short) 7).setNoName());

        inventory.setItem(2, 3, new ItemStackBuilder(Material.ANVIL).setDisplayName("§cBan").build());
        inventory.setItem(2, 4, new ItemStackBuilder(Material.PAPER).setDisplayName("§cMute").build());
        inventory.setItem(2, 5, new ItemStackBuilder(Material.IRON_AXE).setDisplayName("§cKick").build());
        inventory.setItem(2, 6, new ItemStackBuilder(Material.SIGN).setDisplayName("§cReport").build());
        inventory.setItem(2, 7, new ItemStackBuilder(Material.BOOK_AND_QUILL).setDisplayName("§b" + player.language("Entbannungsanträge", "Appeals")).build());

        Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
            JsonObject object = getObject(player.getUniqueId());

            boolean ban = object.get("ban").getAsBoolean();
            boolean mute = object.get("mute").getAsBoolean();
            boolean kick = object.get("kick").getAsBoolean();
            boolean report = object.get("report").getAsBoolean();
            boolean appeals = object.get("appeals").getAsBoolean();

            inventory.setItem(3, 3, new ItemStackBuilder(Material.INK_SACK).setData((short) (ban ? 10 : 8)).setDisplayName(ban ? "§a✔" : "§c✘").build());
            inventory.setItem(3, 4, new ItemStackBuilder(Material.INK_SACK).setData((short) (mute ? 10 : 8)).setDisplayName(mute ? "§a✔" : "§c✘").build());
            inventory.setItem(3, 5, new ItemStackBuilder(Material.INK_SACK).setData((short) (kick ? 10 : 8)).setDisplayName(kick ? "§a✔" : "§c✘").build());
            inventory.setItem(3, 6, new ItemStackBuilder(Material.INK_SACK).setData((short) (report ? 10 : 8)).setDisplayName(report ? "§a✔" : "§c✘").build());
            inventory.setItem(3, 7, new ItemStackBuilder(Material.INK_SACK).setData((short) (appeals ? 10 : 8)).setDisplayName(appeals ? "§a✔" : "§c✘").build());
        });

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CLICK, 10F, 10F);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        OriginPlayer player = OriginManager.getInstance().getPlayer((Player) event.getWhoClicked());

        if (event.getInventory() == null || event.getView() == null || event.getCurrentItem() == null) return;

        InventoryBuilder inventory = (InventoryBuilder) player.openedInventory;

        if (event.getView().getTitle().equals("§8Notify")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.INK_SACK)) {
                Bukkit.getScheduler().runTaskAsynchronously(Origin.getInstance(), () -> {
                    JsonObject object = getObject(player.getUniqueId());

                    switch (event.getSlot()) {
                        case 20:
                            boolean ban = !object.get("ban").getAsBoolean();
                            object.addProperty("ban", ban);
                            setObject(player.getUniqueId(), object);
                            inventory.setItem(3, 3, new ItemStackBuilder(Material.INK_SACK).setData((short) (ban ? 10 : 8)).setDisplayName(ban ? "§a✔" : "§c✘").build());
                            player.sendMessage(prefix + "§7Channel§8: §bBan §8- §7Hooked§8: §a" + (ban ? "§a✔" : "§c✘"));
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
                            break;
                        case 21:
                            boolean mute = !object.get("mute").getAsBoolean();
                            object.addProperty("mute", mute);
                            setObject(player.getUniqueId(), object);
                            inventory.setItem(3, 4, new ItemStackBuilder(Material.INK_SACK).setData((short) (mute ? 10 : 8)).setDisplayName(mute ? "§a✔" : "§c✘").build());
                            player.sendMessage(prefix + "§7Channel§8: §bMute §8- §7Hooked§8: §a" + (mute ? "§a✔" : "§c✘"));
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
                            break;
                        case 22:
                            boolean kick = !object.get("kick").getAsBoolean();
                            object.addProperty("kick", kick);
                            setObject(player.getUniqueId(), object);
                            inventory.setItem(3, 5, new ItemStackBuilder(Material.INK_SACK).setData((short) (kick ? 10 : 8)).setDisplayName(kick ? "§a✔" : "§c✘").build());
                            player.sendMessage(prefix + "§7Channel§8: §bKick §8- §7Hooked§8: §a" + (kick ? "§a✔" : "§c✘"));
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
                            break;
                        case 23:
                            boolean report = !object.get("report").getAsBoolean();
                            object.addProperty("report", report);
                            setObject(player.getUniqueId(), object);
                            inventory.setItem(3, 6, new ItemStackBuilder(Material.INK_SACK).setData((short) (report ? 10 : 8)).setDisplayName(report ? "§a✔" : "§c✘").build());
                            player.sendMessage(prefix + "§7Channel§8: §bReport §8- §7Hooked§8: §a" + (report ? "§a✔" : "§c✘"));
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
                            break;
                        case 24:
                            boolean appeals = !object.get("appeals").getAsBoolean();
                            object.addProperty("appeals", appeals);
                            setObject(player.getUniqueId(), object);
                            inventory.setItem(3, 7, new ItemStackBuilder(Material.INK_SACK).setData((short) (appeals ? 10 : 8)).setDisplayName(appeals ? "§a✔" : "§c✘").build());
                            player.sendMessage(prefix + "§7Channel§8: §b" + player.language("Entbannungsanträge", "Appeals") + " §8- §7Hooked§8: §a" + (appeals ? "§a✔" : "§c✘"));
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10F, 10F);
                            break;
                    }
                });
            }
        }
    }
}
