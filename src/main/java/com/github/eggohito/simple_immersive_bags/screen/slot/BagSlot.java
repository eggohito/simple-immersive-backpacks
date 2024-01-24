package com.github.eggohito.simple_immersive_bags.screen.slot;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class BagSlot extends Slot {

    private final int offsetIndex;

    public BagSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index - PlayerScreenHandler.INVENTORY_END, x, y);
        this.offsetIndex = index;
    }

    @Override
    public int getIndex() {
        return offsetIndex;
    }

    @Override
    public boolean canInsert(ItemStack stack) {

        if (!(stack.getItem() instanceof BagItem)) {
            return super.canInsert(stack);
        }

        NbtCompound stackNbt;
        if ((stackNbt = stack.getNbt()) == null || !stackNbt.contains(SimpleImmersiveBags.ITEM_CONTAINER_ID)) {
            return true;
        }

        NbtCompound itemContainerNbt = stackNbt.getCompound(SimpleImmersiveBags.ITEM_CONTAINER_ID);
        NbtList containedStacksNbt = itemContainerNbt.getList("Items", NbtElement.COMPOUND_TYPE);

        DefaultedList<ItemStack> containedStacks = DefaultedList.ofSize(containedStacksNbt.size(), ItemStack.EMPTY);
        Inventories.readNbt(itemContainerNbt, containedStacks);

        return containedStacks.isEmpty();

    }

}
