package com.github.eggohito.simple_immersive_bags.screen.slot;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.inventory.DelegatedBagInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BagSlot extends Slot {

    private final int offsetIndex;

    public BagSlot(BagInventory inventory, int index, int x, int y) {
        super(inventory, index - PlayerScreenHandler.INVENTORY_END, x, y);
        this.offsetIndex = index;
    }

    @Override
    public int getIndex() {
        return offsetIndex;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(stack, null);
        return inventory instanceof DelegatedBagInventory
            || (bagContainer==null||bagContainer.getContents(stack).isEmpty());
    }

}
