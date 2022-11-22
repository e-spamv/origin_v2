package de.claved.origin.spigot.api;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import de.claved.origin.spigot.Origin;
import de.claved.origin.spigot.api.manager.*;
import de.claved.origin.spigot.api.scoreboard.Scoreboard;
import de.claved.origin.utils.Reflections;
import de.claved.origin.utils.enums.Language;
import de.claved.origin.utils.enums.Rank;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class OriginPlayer {

    private Player player;

    private final UUID uuid;
    private final String name;

    private Language language;
    private Rank rank;

    private final String socketAddress;
    private final String firstLogin;
    private final String lastLogin;
    private String server;

    private int coins;
    private int points;
    private int onlineTime;

    private boolean nickState;

    public Scoreboard scoreboard;
    public Inventory openedInventory;

    public OriginPlayer(JsonObject object) {
        this.uuid = UUID.fromString(object.get("uuid").getAsString());
        this.name = object.get("name").getAsString();

        language = Language.valueOf(object.get("language").getAsString());
        rank = Rank.valueOf(object.get("rank").getAsString());

        socketAddress = object.get("socketAddress").getAsString();
        firstLogin = object.get("firstLogin").getAsString();
        lastLogin = object.get("lastLogin").getAsString();
        server = object.get("server").getAsString();

        coins = object.get("coins").getAsInt();
        points = object.get("points").getAsInt();
        onlineTime = object.get("onlineTime").getAsInt();

        nickState = object.get("nickState").getAsBoolean();
    }

    public Player getBukkitPlayer() {
        return player;
    }

    public void setBukkitPlayer(Player player) {
        this.player = player;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Language getLanguage() {
        return language;
    }

    public <T> T language(T german, T english) {
        return (language.equals(Language.GERMAN) ? german : english);
    }

    public void setLanguage(Language language) {
        this.language = language;
        LanguageManager.getInstance().setLanguage(uuid, language);
    }

    public void updateLanguage() {
        language = LanguageManager.getInstance().getLanguage(uuid);
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public Rank getRank() {
        if (NickManager.getInstance().getNickName(uuid) != null) {
            return Rank.PLAYER;
        }
        return rank;
    }

    public Rank getRealRank() {
        return rank;
    }

    public void updateRank() {
        rank = PermissionManager.getInstance().getRank(uuid);
    }

    public boolean hasPriorityAccess(int needed) {
        return needed <= getRealRank().getPriority();
    }

    public String getDisplayName() {
        return getRank().getColor() + (getNick() != null ? getNick() : name);
    }

    public String getRealDisplayName() {
        return getRealRank().getColor() + name;
    }

    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    public void sendMessage(String german, String english) {
        sendMessage(language(german, english));
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
        CoinsManager.getInstance().setCoins(uuid, this.coins);
    }

    public void addCoins(int coins) {
        this.coins = (this.coins + coins);
        CoinsManager.getInstance().setCoins(uuid, this.coins);
    }

    public void removeCoins(int coins) {
        this.coins = (this.coins - coins);
        CoinsManager.getInstance().setCoins(uuid, this.coins);
    }

    public void updateCoins() {
        coins = CoinsManager.getInstance().getCoins(uuid);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        PointsManager.getInstance().setPoints(uuid, this.points);
    }

    public void addPoints(int points) {
        this.points = (this.points + points);
        PointsManager.getInstance().setPoints(uuid, this.points);
    }

    public void removePoints(int points) {
        this.points = (this.points - points);
        PointsManager.getInstance().setPoints(uuid, this.points);
    }

    public void updatePoints() {
        points = PointsManager.getInstance().getPoints(uuid);
    }

    public int getOnlineTime() {
        return onlineTime;
    }

    public void updateOnlineTime() {
        onlineTime = OnlineTimeManager.getInstance().getOnlineTime(uuid);
    }

    public String getNick() {
        if (NickManager.getInstance().getNickName(uuid) != null) {
            return NickManager.getInstance().getNickName(uuid);
        }
        return null;
    }

    public boolean isNicked() {
        return nickState;
    }

    public void setNickState(boolean state) {
        this.nickState = state;
        NickManager.getInstance().setNickState(uuid, this.nickState);
    }

    public boolean isOnline() {
        return getServer() != null;
    }

    public boolean isLocalOnline() {
        return Bukkit.getOnlinePlayers().contains(player);
    }

    public String getServer() {
        if (server.equals("-")) return null;
        return server;
    }

    public void updateServer() {
        server = LoginManager.getInstance().getServer(uuid);
    }

    public ItemStack getItemInHand() {
        return getBukkitPlayer() != null ? getBukkitPlayer().getItemInHand() : null;
    }

    public PlayerInventory getInventory() {
        return getBukkitPlayer().getInventory();
    }

    public InventoryView getOpenInventory() {
        return getBukkitPlayer().getOpenInventory();
    }

    public void openInventory(Inventory inventory) {
        openedInventory = inventory;
        getBukkitPlayer().openInventory(inventory);
    }

    public Inventory getOpenedInventory() {
        return openedInventory;
    }

    public void closeInventory() {
        getBukkitPlayer().closeInventory();
    }

    public void updateInventory() {
        getBukkitPlayer().updateInventory();
    }

    public Scoreboard getScoreboard() {
        if (scoreboard == null) {
            scoreboard = new Scoreboard(this);
        }
        return scoreboard;
    }

    public void kick(String reason) {
        if (getBukkitPlayer() != null) getBukkitPlayer().kickPlayer(reason);
    }

    public void kick(String german, String english) {
        if (getBukkitPlayer() != null) getBukkitPlayer().kickPlayer(language(german, english));
    }

    public void setMetadata(String value, FixedMetadataValue fixedMetadataValue) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setMetadata(value, fixedMetadataValue);
    }

    public Location getLocation() {
        return getBukkitPlayer() != null ? getBukkitPlayer().getLocation() : null;
    }

    public void setVelocity(Vector velocity) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setVelocity(velocity);
    }

    public void playSound(Location location, Sound sound, float volume, float pitch) {
        if (getBukkitPlayer() != null) getBukkitPlayer().playSound(location, sound, volume, pitch);
    }

    public void playEffect(Location location, Effect effect, int integer) {
        if (getBukkitPlayer() != null) getBukkitPlayer().playEffect(location, effect, integer);
    }

    public void addPotionEffect(PotionEffect potionEffect) {
        if (getBukkitPlayer() != null) getBukkitPlayer().addPotionEffect(potionEffect);
    }

    public void setMaximumNoDamageTicks(int ticks) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setMaximumNoDamageTicks(ticks);
    }

    public void setNoDelay(boolean value) {
        setMaximumNoDamageTicks((value ? 0 : 20));
    }

    public void showPlayer(OriginPlayer player) {
        if (player == this) return;
        if (getBukkitPlayer() == null || player.getBukkitPlayer() == null) return;
        getBukkitPlayer().showPlayer(player.getBukkitPlayer());
    }

    public void hidePlayer(OriginPlayer player) {
        if (player == this) return;
        if (getBukkitPlayer() == null || player.getBukkitPlayer() == null) return;
        getBukkitPlayer().hidePlayer(player.getBukkitPlayer());
    }

    public boolean canSee(OriginPlayer player) {
        return getBukkitPlayer() != null && player.getBukkitPlayer() != null && (player == this || getBukkitPlayer().canSee(player.getBukkitPlayer()));
    }

    public World getWorld() {
        return player.getWorld();
    }

    public void teleport(OriginPlayer player) {
        teleport(player.getLocation());
    }

    public void teleport(Location location) {
        if (getBukkitPlayer() != null) getBukkitPlayer().teleport(location);
    }

    public int getLevel() {
        return (getBukkitPlayer() != null ? getBukkitPlayer().getLevel() : 0);
    }

    public void setLevel(int level) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setLevel(level);
    }

    public float getExp() {
        return (getBukkitPlayer() != null ? getBukkitPlayer().getExp() : 0);
    }

    public void setExp(float value) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setExp(value);
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return (getBukkitPlayer() != null ? getBukkitPlayer().getActivePotionEffects() : Collections.emptyList());
    }

    public void removePotionEffect(PotionEffectType type) {
        if (getBukkitPlayer() != null) getBukkitPlayer().removePotionEffect(type);
    }

    public void damage(double value) {
        if (getBukkitPlayer() != null) getBukkitPlayer().damage(value);
    }

    public void respawn() {
        if (getBukkitPlayer() != null) getBukkitPlayer().spigot().respawn();
    }

    public double getHealth() {
        return (getBukkitPlayer() != null ? getBukkitPlayer().getHealth() : 0);
    }

    public void setHealth(double value) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setHealth(value);
    }

    public void setHealthScale(double value) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setHealthScale(value);
    }

    public void setFoodLevel(int level) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setFoodLevel(level);
    }

    public void setSaturation(float value) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setSaturation(value);
    }

    public void setFireTicks(int ticks) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setFireTicks(ticks);
    }

    public boolean isFlying() {
        return getBukkitPlayer() != null && getBukkitPlayer().isFlying();
    }

    public void setFlying(boolean value) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setFlying(value);
    }

    public boolean getAllowFlight() {
        return getBukkitPlayer() != null && getBukkitPlayer().getAllowFlight();
    }

    public void setAllowFlight(boolean value) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setAllowFlight(value);
    }

    public GameMode getGameMode() {
        return (getBukkitPlayer() != null ? getBukkitPlayer().getGameMode() : GameMode.SURVIVAL);
    }

    public void setGameMode(GameMode mode) {
        if (getBukkitPlayer() != null) getBukkitPlayer().setGameMode(mode);
    }

    public void sendActionbar(String message) {
        sendPacket(new PacketPlayOutChat(new ChatComponentText(message), (byte) 2));
    }

    public void sendActionbar(String german, String english) {
        sendPacket(new PacketPlayOutChat(new ChatComponentText(language(german, english)), (byte) 2));
    }

    public void sendPluginMessage(Plugin plugin, String value, byte[] input) {
        if (getBukkitPlayer() != null) getBukkitPlayer().sendPluginMessage(plugin, value, input);
    }

    public void send(String server) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("Connect");
        byteArrayDataOutput.writeUTF(server);
        player.sendPluginMessage(Origin.getInstance(), "BungeeCord", byteArrayDataOutput.toByteArray());
    }

    public void sendPacket(Object packet) {
        Preconditions.checkArgument(Objects.requireNonNull(OriginManager.getInstance().getVersionSpecificClass("net.minecraft.server.version.Packet")).isAssignableFrom(packet.getClass()), "Object not instanceof minecraft packet");
        try {
            Object playerConnection = getPlayerConnection();
            Method sendPacket = Reflections.getMethod(Objects.requireNonNull(OriginManager.getInstance().getVersionSpecificClass("net.minecraft.server.version.PlayerConnection")), "sendPacket", OriginManager.getInstance().getVersionSpecificClass("net.minecraft.server.version.Packet"));
            Reflections.invokeMethod(sendPacket, playerConnection, packet);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public Object getPlayerConnection() {
        Method getHandle = Reflections.getMethod(Objects.requireNonNull(OriginManager.getInstance().getVersionSpecificClass("org.bukkit.craftbukkit.version.entity.CraftPlayer")), "getHandle");
        Object handle = Reflections.invokeMethod(getHandle, player);
        return Reflections.getField(handle, "playerConnection");
    }

    public void editPermissibleBase() throws Exception {
        Player player = getBukkitPlayer();
        Field field = OriginManager.getInstance().getVersionSpecificClass("org.bukkit.craftbukkit.version.entity.CraftHumanEntity").getDeclaredField("perm");
        field.setAccessible(true);
        field.set(player, new PermissionManager.PermissibleOverride(player, this));
        field.setAccessible(false);
    }
}
