package com.github.eggohito.simple_immersive_bags.util;

import net.minecraft.screen.PlayerScreenHandler;

public class BagUtil {

    public static boolean withinEquipmentBounds(int slotIndex) {
        return slotIndex >= PlayerScreenHandler.EQUIPMENT_START && slotIndex < PlayerScreenHandler.EQUIPMENT_END;
    }

}
