package com.github.eggohito.simple_immersive_bags.inventory;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class BagInventory extends GridInventory {

    private final Identifier screenTextureId;
    private final ItemStack sourceStack;

    private final boolean save;
    private boolean dirty;

    public BagInventory(ItemStack sourceStack, Identifier screenTextureId, boolean shouldSave, int rows, int columns) {
        super(rows, columns);
        this.screenTextureId = screenTextureId;
        this.sourceStack = sourceStack;
        this.save = shouldSave;
    }

    public BagInventory(ItemStack sourceStack, Identifier screenTextureId, int rows, int columns) {
        super(rows, columns);
        this.screenTextureId = screenTextureId;
        this.sourceStack = sourceStack;
        this.save = true;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.load();
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.getWorld().isClient && dirty && save) {
            ((EntityBagUpdateStatus) player).sib$setStatus(BagUpdateStatus.SAVE);
            this.save();
        }
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    public Identifier getScreenTextureId() {
        return screenTextureId;
    }

    public ItemStack getSourceStack() {
        return sourceStack;
    }

    public boolean saveable() {
        return save;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void load() {

        if (!(sourceStack.getItem() instanceof BagItem)) {
            SimpleImmersiveBags.LOGGER.warn("Tried loading the contents of bag inventory of item {}, which isn't a bag item!", sourceStack);
            return;
        }

        NbtCompound stackNbt;
        if ((stackNbt = sourceStack.getNbt()) != null && stackNbt.contains(SimpleImmersiveBags.ITEM_CONTAINER_ID)) {
            Inventories.readNbt(stackNbt.getCompound(SimpleImmersiveBags.ITEM_CONTAINER_ID), this.getHeldStacks());
        }

    }

    public void save() {

        if (!save) {
            SimpleImmersiveBags.LOGGER.warn("Tried saving the contents of the bag inventory of item {}, which is not saveable!", sourceStack);
            return;
        }

        if (!(sourceStack.getItem() instanceof BagItem)) {
            SimpleImmersiveBags.LOGGER.warn("Tried saving the contents of the bag inventory of item {}, which isn't a bag item!", sourceStack);
            return;
        }

        NbtCompound itemContainerNbt = sourceStack.getOrCreateSubNbt(SimpleImmersiveBags.ITEM_CONTAINER_ID);
        Inventories.writeNbt(itemContainerNbt, this.getHeldStacks());

        this.dirty = false;

    }

}
