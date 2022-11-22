package de.claved.origin.spigot.api.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemStackBuilder extends ItemStack {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private final List<String> lore = new ArrayList<>();

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemStackBuilder(Material material) {
        this.itemStack = new ItemStack(material, 1, (short) 0);
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStackBuilder amount(int value) {
        this.itemStack.setAmount(value);
        return this;
    }

    public ItemStackBuilder setNoName() {
        this.itemMeta.setDisplayName(" ");
        return this;
    }

    public ItemStackBuilder setGlow() {
        this.itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemStackBuilder setData(short data) {
        this.itemStack.setDurability(data);
        return this;
    }

    public ItemStackBuilder addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }

    public ItemStackBuilder setDisplayName(String name) {
        this.itemMeta.setDisplayName(name);
        return this;
    }

    public ItemStackBuilder setSkullOwner(String owner) {
        ((SkullMeta) this.itemMeta).setOwner(owner);
        return this;
    }

    public ItemStackBuilder setColor(Color color) {
        ((LeatherArmorMeta) this.itemMeta).setColor(color);
        return this;
    }

    public ItemStackBuilder setBannerColor(DyeColor dyeColor) {
        ((BannerMeta) this.itemMeta).setBaseColor(dyeColor);
        return this;
    }

    public ItemStackBuilder addPattern(DyeColor dyeColor, PatternType patternType) {
        ((BannerMeta) this.itemMeta).addPattern(new Pattern(dyeColor, patternType));
        return this;
    }

    public ItemStackBuilder setFireworkEffectMeta(Color color) {
        FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) itemMeta;
        FireworkEffect.Builder builder = FireworkEffect.builder();

        builder.withColor(color);
        fireworkEffectMeta.setEffect(builder.build());
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        this.setItemMeta(fireworkEffectMeta);

        return this;
    }

    public ItemStackBuilder setUnbreakable(boolean value) {
        this.itemMeta.spigot().setUnbreakable(value);
        return this;
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int lvl) {
        this.itemMeta.addEnchant(enchantment, lvl, true);
        return this;
    }

    public ItemStackBuilder addItemFlag(ItemFlag itemFlag) {
        this.itemMeta.addItemFlags(itemFlag);
        return this;
    }

    public ItemStackBuilder addAllFlags() {
        this.itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    public ItemStackBuilder addLeatherColor(Color color) {
        ((LeatherArmorMeta) this.itemMeta).setColor(color);
        return this;
    }

    public boolean equals(ItemStack itemStack) {
        return itemStack.getType().equals(this.getType()) && itemStack.getAmount() == this.getAmount() && itemStack.getDurability() == this.getDurability() && (itemStack.getItemMeta() == null) == (this.getItemMeta() == null) && (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName().equals(this.getItemMeta().getDisplayName()));
    }

    public ItemStack build() {
        if (!lore.isEmpty()) {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getSkull(String skinWebUrl, String name) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (skinWebUrl == null)
            return itemStack;
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", skinWebUrl).getBytes());
        gameProfile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field field;
        try {
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, gameProfile);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        skullMeta.setDisplayName(name);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }
}
