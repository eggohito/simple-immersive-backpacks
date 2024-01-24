package com.github.eggohito.simple_immersive_bags.api;

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

}
