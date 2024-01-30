package com.github.eggohito.simple_immersive_bags.util;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;

public class BagUtil {

    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = {
        EquipmentSlot.OFFHAND,
        EquipmentSlot.MAINHAND,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET,
        EquipmentSlot.HEAD
    };

    public static boolean withinEquipmentBounds(int slotIndex) {
        return slotIndex >= PlayerScreenHandler.EQUIPMENT_START && slotIndex < PlayerScreenHandler.EQUIPMENT_END;
    }

    public static boolean withinEquipmentBounds(Entity entity, Slot slot) {
        return withinEquipmentBounds(slot.id);
    }

    public static Optional<EquipmentSlot> getFirstOpenedBag(LivingEntity entity) {

        for (EquipmentSlot slot : EQUIPMENT_SLOT_ORDER) {

            ItemStack equippedStack = entity.getEquippedStack(slot);
            BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(equippedStack, null);

            if (bagContainer != null && bagContainer.getState(equippedStack) == BagState.OPENED) {
                return Optional.of(slot);
            }

        }

        return Optional.empty();

    }

}
