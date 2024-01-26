package com.github.eggohito.simple_immersive_bags.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class DelegatedBagInventory extends BagInventory {

    private final GridInventory delegate;

    public DelegatedBagInventory(ItemStack sourceStack, Identifier screenTextureId, GridInventory inventory) {
        super(sourceStack, screenTextureId, false, false, inventory.getRows(), inventory.getColumns());
        this.delegate = inventory;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        super.onOpen(player);
        delegate.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        super.onClose(player);
        delegate.onClose(player);
    }

    @Override
    public void markDirty() {
        delegate.markDirty();
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public void addListener(InventoryChangedListener listener) {
        delegate.addListener(listener);
    }

    @Override
    public void removeListener(InventoryChangedListener listener) {
        delegate.removeListener(listener);
    }

    @Override
    public ItemStack getStack(int slot) {
        return delegate.getStack(slot);
    }

    @Override
    public List<ItemStack> clearToList() {
        return delegate.clearToList();
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return delegate.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeItem(Item item, int count) {
        return delegate.removeItem(item, count);
    }

    @Override
    public ItemStack addStack(ItemStack stack) {
        return delegate.addStack(stack);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return delegate.canInsert(stack);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return delegate.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        delegate.setStack(slot, stack);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return delegate.canPlayerUse(player);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        delegate.provideRecipeInputs(finder);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void readNbtList(NbtList nbtList) {
        delegate.readNbtList(nbtList);
    }

    @Override
    public NbtList toNbtList() {
        return delegate.toNbtList();
    }

    @Override
    public DefaultedList<ItemStack> getHeldStacks() {
        return delegate.getHeldStacks();
    }

    @Override
    public int getMaxCountPerStack() {
        return delegate.getMaxCountPerStack();
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return delegate.isValid(slot, stack);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return delegate.canTransferTo(hopperInventory, slot, stack);
    }

}
