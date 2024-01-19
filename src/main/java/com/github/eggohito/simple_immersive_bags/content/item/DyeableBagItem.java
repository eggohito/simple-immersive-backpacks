package com.github.eggohito.simple_immersive_bags.content.item;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class DyeableBagItem extends BagItem implements DyeableItem {

    public DyeableBagItem(ArmorMaterial armorMaterial, Identifier screenTextureId, int initialRows, int initialColumns) {
        super(armorMaterial, screenTextureId, initialRows, initialColumns);
    }

    public static float[] unpackRgb(ItemStack stack) {

        float[] rgb = new float[] {1.0f, 1.0f, 1.0f};
        if (!(stack.getItem() instanceof DyeableBagItem dyeableBagItem)) {
            return rgb;
        }

        int i = dyeableBagItem.getColor(stack);

        rgb[0] = (float) (i >> 16 & 255) / 255.0f;
        rgb[1] = (float) (i >> 8 & 255) / 255.0f;
        rgb[2] = (float) (i & 255) / 255.0f;

        return rgb;


    }

}
