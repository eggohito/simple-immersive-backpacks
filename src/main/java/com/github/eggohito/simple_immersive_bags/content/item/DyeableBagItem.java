package com.github.eggohito.simple_immersive_bags.content.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class DyeableBagItem extends BagItem implements DyeableItem {

    public DyeableBagItem(Identifier screenTextureId, EquipmentSlot equipSlot, int initialRows, int initialColumns) {
        super(screenTextureId, equipSlot, initialRows, initialColumns);
    }

    public DyeableBagItem(Identifier screenTextureId, EquipmentSlot equipSlot, SoundEvent equipSound, int initialRows, int initialColumns) {
        super(screenTextureId, equipSlot, equipSound, initialRows, initialColumns);
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
