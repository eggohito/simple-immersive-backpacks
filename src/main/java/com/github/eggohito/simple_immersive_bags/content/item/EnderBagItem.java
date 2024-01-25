package com.github.eggohito.simple_immersive_bags.content.item;

import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.inventory.DelegatedBagInventory;
import com.github.eggohito.simple_immersive_bags.inventory.DelegatedGridInventory;
import com.github.eggohito.simple_immersive_bags.inventory.GridInventory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class EnderBagItem extends BagItem {

    public EnderBagItem(Identifier screenTextureId, EquipmentSlot equipSlot) {
        super(screenTextureId, equipSlot, 3, 9);
    }

    public EnderBagItem(Identifier screenTextureId, EquipmentSlot equipSlot, SoundEvent equipSound) {
        super(screenTextureId, equipSlot, equipSound, 3, 9);
    }

    @Override
    public BagInventory asDelegatedBagInventory(LivingEntity holder, ItemStack stack) {

        if (!stack.isOf(this) || !(holder instanceof PlayerEntity player)) {
            return BagInventory.EMPTY;
        }

        GridInventory enderGridInventory = new DelegatedGridInventory(player.getEnderChestInventory(), initialRows, initialColumns);
        return new DelegatedBagInventory(stack, screenTextureId, enderGridInventory);

    }

}
