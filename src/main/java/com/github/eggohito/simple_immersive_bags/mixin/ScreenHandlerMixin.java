package com.github.eggohito.simple_immersive_bags.mixin;

import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow public abstract Slot getSlot(int index);

    @Shadow public abstract ItemStack getCursorStack();

    @Shadow public abstract boolean isValid(int slot);

    @Inject(method = "onSlotClick", at = @At("HEAD"))
    private void sib$saveBagInventory(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {

        if (player.getWorld().isClient) {
            return;
        }

        switch (actionType) {
            case QUICK_MOVE -> {

                if (this.outsideEquipmentBounds(slotIndex)) {
                    return;
                }

                if (this.getSlot(slotIndex).getStack().getItem() instanceof BagItem && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                    bagScreenHandler.getBagInventory().save();
                }

            }
            case PICKUP -> {

                if (!this.isValid(slotIndex) || this.outsideEquipmentBounds(slotIndex)) {
                    return;
                }

                ItemStack stackInSlot = this.getSlot(slotIndex).getStack();
                if (ItemStack.areEqual(stackInSlot, this.getCursorStack())) {
                    return;
                }

                if (stackInSlot.getItem() instanceof BagItem && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                    bagScreenHandler.getBagInventory().save();
                }

            }
            case SWAP -> {

                if (!(button >= 0 && button < 9 || button == 40) || this.outsideEquipmentBounds(slotIndex)) {
                    return;
                }

                Slot slot = this.getSlot(slotIndex);

                ItemStack stackInSwapSlot = player.getInventory().getStack(button);
                ItemStack stackInSlot = slot.getStack();

                if (ItemStack.areEqual(stackInSwapSlot, stackInSlot)) {
                    return;
                }

                if (stackInSwapSlot.isEmpty() && slot.canTakeItems(player) && stackInSlot.getItem() instanceof BagItem && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                    bagScreenHandler.getBagInventory().save();
                }

                else if (slot.canTakeItems(player) && slot.canInsert(stackInSwapSlot)) {

                    if (stackInSlot.getItem() instanceof BagItem && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                        bagScreenHandler.getBagInventory().save();
                    }

                }

            }
            case THROW -> {

                if (!this.getCursorStack().isEmpty()) {
                    return;
                }

                if (this.getSlot(slotIndex).getStack().getItem() instanceof BagItem && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                    bagScreenHandler.getBagInventory().save();
                }

            }
        }

    }

    @Unique
    private ScreenHandler thisAsScreenHandler() {
        return (ScreenHandler) (Object) this;
    }

    @Unique
    private boolean outsideEquipmentBounds(int slotIndex) {
        return slotIndex < PlayerScreenHandler.EQUIPMENT_START
            || slotIndex >= PlayerScreenHandler.EQUIPMENT_END;
    }

}
