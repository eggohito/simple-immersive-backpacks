package com.github.eggohito.simple_immersive_bags.content.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class EnderBagItem extends BagItem {

    public EnderBagItem(ArmorMaterial armorMaterial, Identifier screenTextureId, int initialRows, int initialColumns) {
        super(armorMaterial, screenTextureId, initialRows, initialColumns);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return null;    //  TODO: Implement the screen handler for ender bag items
    }

}
