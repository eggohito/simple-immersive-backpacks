package com.github.eggohito.simple_immersive_bags.inventory;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class BagInventory extends SimpleInventory {

    private final ItemStack sourceStack;

    private final int rows;
    private final int columns;

    public boolean dirty;

    public BagInventory(ItemStack sourceStack, int rows, int columns) {
        super(rows * columns);
        this.sourceStack = sourceStack;
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public void onOpen(PlayerEntity player) {

        NbtCompound itemContainerNbt;
        if (!(sourceStack.getItem() instanceof BagItem)) {
            return;
        }

        if ((itemContainerNbt = sourceStack.getNbt()) != null && itemContainerNbt.contains(SimpleImmersiveBags.ITEM_CONTAINER_ID)) {
            Inventories.readNbt(itemContainerNbt, this.getHeldStacks());
        }

    }

    @Override
    public void onClose(PlayerEntity player) {

        if (!player.getWorld().isClient && sourceStack.getItem() instanceof BagItem) {
            this.save();
        }

    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void save() {
        NbtCompound itemContainerNbt = sourceStack.getOrCreateSubNbt(SimpleImmersiveBags.ITEM_CONTAINER_ID);
        Inventories.writeNbt(itemContainerNbt, this.getHeldStacks());
    }

}
