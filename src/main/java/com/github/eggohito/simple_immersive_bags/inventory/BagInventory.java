package com.github.eggohito.simple_immersive_bags.inventory;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

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

        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(sourceStack, null);
        if (bagContainer == null) {
            SimpleImmersiveBags.LOGGER.error("Tried loading the bag inventory contents of item {}, which isn't a bag item!", sourceStack);
            return;
        }

        DefaultedList<ItemStack> contents = bagContainer.getContents(sourceStack);
        for (int i = 0; i < this.size(); i++) {
            this.getHeldStacks().set(i, contents.get(i));
        }

    }

    public void save() {

        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(sourceStack, null);
        if (bagContainer == null) {
            SimpleImmersiveBags.LOGGER.warn("Tried saving the bag inventory contents of item {}, which isn't a bag item!", sourceStack);
            return;
        }

        if (!save) {
            SimpleImmersiveBags.LOGGER.warn("Tried saving the bag inventory contents of item {}, which can't be saved!", sourceStack);
            return;
        }

        bagContainer.setContents(sourceStack, this.getHeldStacks());
        this.dirty = false;

    }

}
