package com.github.eggohito.simple_immersive_bags.api;

import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.util.BagState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface BagContainer {

    DefaultedList<ItemStack> getContents(ItemStack sourceStack);

    void setContents(ItemStack sourceStack, DefaultedList<ItemStack> replacement);

    default boolean isEmpty(ItemStack sourceStack) {
        return this.getContents(sourceStack)
            .stream()
            .allMatch(ItemStack::isEmpty);
    }

    BagState getState(ItemStack sourceStack);

    void setState(ItemStack sourceStack, BagState state);

    BagInventory asBagInventory(ItemStack sourceStack);

    BagInventory asDelegatedBagInventory(LivingEntity holder, ItemStack sourceStack);

}
