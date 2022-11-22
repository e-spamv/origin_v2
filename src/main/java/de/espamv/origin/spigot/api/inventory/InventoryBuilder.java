package de.claved.origin.spigot.api.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

public class InventoryBuilder implements Inventory {

    private final Inventory inventory;

    public InventoryBuilder(int rows, String title) {
        inventory = Bukkit.createInventory(null, rows * 9, title);
    }

    public void setItem(int row, int line, ItemStack item) {
        int slot = (row - 1) * 9 + line - 1;
        setItem(slot, item);
    }

    public ItemStackBuilder getItem(int row, int line) {
        int slot = (row - 1) * 9 + line - 1;
        return (ItemStackBuilder) getItem(slot);
    }

    public void clear() {
        inventory.clear();
    }

    public void clear(int row) {
        for (int i = 0; i < 9; ++i) {
            int slot = (row - 1) * 9 + i;
            setItem(slot, null);
        }
    }

    public void clear(int... rows) {
        for (int i : rows) {
            clear(i);
        }
    }

    public void fill(ItemStackBuilder item) {
        ItemStack[] contents = this.getContents();
        for (int i = 0; i < contents.length; ++i) {
            if (contents[i] == null || contents[i].getType().equals(Material.AIR)) {
                contents[i] = item.build();
            }
        }
        this.setContents(contents);
    }

    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return inventory.all(material);
    }

    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        return inventory.all(itemStack);
    }

    @Deprecated
    public HashMap<Integer, ? extends ItemStack> all(int i) {
        return inventory.all(i);
    }

    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return inventory.addItem(itemStacks);
    }

    public boolean contains(Material material) throws IllegalArgumentException {
        return inventory.contains(material);
    }

    public boolean contains(ItemStack itemStack) {
        return inventory.contains(itemStack);
    }

    public boolean contains(Material material, int i) throws IllegalArgumentException {
        return inventory.contains(material, i);
    }

    public boolean contains(ItemStack itemStack, int i) {
        return inventory.contains(itemStack, i);
    }

    public boolean containsAtLeast(ItemStack itemStack, int i) {
        return inventory.containsAtLeast(itemStack, i);
    }

    @Deprecated
    public boolean contains(int i) {
        return inventory.contains(i);
    }

    @Deprecated
    public boolean contains(int i, int i1) {
        return inventory.contains(i, i1);
    }

    public void forEach(Consumer<? super ItemStack> action) {
        inventory.forEach(action);
    }

    public int first(Material material) throws IllegalArgumentException {
        return inventory.first(material);
    }

    public int first(ItemStack itemStack) {
        return inventory.first(itemStack);
    }

    public int firstEmpty() {
        return inventory.firstEmpty();
    }

    @Deprecated
    public int first(int i) {
        return inventory.first(i);
    }

    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    public int getSize() {
        return inventory.getSize();
    }

    public InventoryHolder getHolder() {
        return inventory.getHolder();
    }

    public InventoryType getType() {
        return inventory.getType();
    }

    public ItemStack getItem(int i) {
        return inventory.getItem(i);
    }

    public ItemStack[] getContents() {
        return inventory.getContents();
    }

    public List<HumanEntity> getViewers() {
        return inventory.getViewers();
    }

    public String getName() {
        return inventory.getName();
    }

    public String getTitle() {
        return inventory.getTitle();
    }

    public ListIterator<ItemStack> iterator() {
        return inventory.iterator();
    }

    public ListIterator<ItemStack> iterator(int i) {
        return inventory.iterator(i);
    }

    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return inventory.removeItem(itemStacks);
    }

    public void remove(Material material) throws IllegalArgumentException {
        inventory.remove(material);
    }

    public void remove(ItemStack itemStack) {
        inventory.remove(itemStack);
    }

    @Deprecated
    public void remove(int i) {
        inventory.remove(i);
    }

    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        inventory.setContents(itemStacks);
    }

    public void setItem(int i, ItemStack itemStack) {
        inventory.setItem(i, itemStack);
    }

    public void setMaxStackSize(int i) {
        inventory.setMaxStackSize(i);
    }
}
