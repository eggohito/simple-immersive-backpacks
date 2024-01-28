package com.github.eggohito.simple_immersive_bags.util;

import net.minecraft.entity.Entity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BagUtil {

    public static boolean withinEquipmentBounds(int slotIndex) {
        return slotIndex >= PlayerScreenHandler.EQUIPMENT_START && slotIndex < PlayerScreenHandler.EQUIPMENT_END;
    }

    public static boolean withinEquipmentBounds(Entity entity, Slot slot) {
        return withinEquipmentBounds(slot.id);
    }

}
