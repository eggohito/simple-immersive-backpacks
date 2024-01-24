package com.github.eggohito.simple_immersive_bags.content.item;

import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.inventory.DelegatedBagInventory;
import com.github.eggohito.simple_immersive_bags.inventory.DelegatedGridInventory;
import com.github.eggohito.simple_immersive_bags.inventory.GridInventory;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class EnderBagItem extends BagItem {

    public EnderBagItem(ArmorMaterial armorMaterial, Identifier screenTextureId) {
        super(armorMaterial, screenTextureId, 3, 9);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {

        ItemStack equippedStack = player.getEquippedStack(this.getSlotType());
        if (!equippedStack.isOf(this)) {
            return null;
        }

        GridInventory enderGridInventory = new DelegatedGridInventory(player.getEnderChestInventory(), initialRows, initialColumns);
        BagInventory enderBagInventory = new DelegatedBagInventory(equippedStack, screenTextureId, enderGridInventory);

        return new BagScreenHandler(syncId, playerInventory, player, enderBagInventory);

    }

}
