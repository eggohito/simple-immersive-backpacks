package com.github.eggohito.simple_immersive_bags.mixin;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.github.eggohito.simple_immersive_bags.util.BagState;
import com.github.eggohito.simple_immersive_bags.util.BagUtil;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow public abstract ItemStack getCursorStack();

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;onPickupSlotClick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/ClickType;)V"))
    private void sib$saveBagOnPickup(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, @Local Slot slot) {

        if (player.getWorld().isClient || button == 1) {
            return;
        }

        ItemStack slotStack = slot.getStack();

        if (!BagUtil.withinEquipmentBounds(player, slot) || ItemStack.areEqual(slotStack, this.getCursorStack())) {
            return;
        }

        BagContainer slotBagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(slotStack, null);

        if (slotBagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
            bagScreenHandler.getBagInventory().onClose(player);
            slotBagContainer.setState(slotStack, BagState.CLOSED);
        }

    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private void sib$saveBagOnQuickMove(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, @Local Slot slot) {

        if (player.getWorld().isClient) {
            return;
        }

        ItemStack slotStack = slot.getStack();
        BagContainer slotBagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(slotStack, null);

        if (slotBagContainer == null || !BagUtil.withinEquipmentBounds(player, slot)) {
            return;
        }

        if (thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
            bagScreenHandler.getBagInventory().onClose(player);
            slotBagContainer.setState(slotStack, BagState.CLOSED);
        }

    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/screen/slot/SlotActionType;SWAP:Lnet/minecraft/screen/slot/SlotActionType;")))
    private void sib$saveBagOnSwap(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, @Local Slot slot) {

        if (player.getWorld().isClient) {
            return;
        }

        ItemStack swapStack = player.getInventory().getStack(button);
        ItemStack slotStack = slot.getStack();

        if (!BagUtil.withinEquipmentBounds(player, slot) || ItemStack.areEqual(swapStack, slotStack)) {
            return;
        }

        if ((swapStack.isEmpty() && slot.canTakeItems(player)) || (slot.canTakeItems(player) && slot.canInsert(swapStack))) {

            BagContainer slotBagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(slotStack, null);

            if (slotBagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                bagScreenHandler.getBagInventory().onClose(player);
                slotBagContainer.setState(slotStack, BagState.CLOSED);
            }

        }

    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/ItemEntity;", ordinal = 0), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/screen/slot/SlotActionType;THROW:Lnet/minecraft/screen/slot/SlotActionType;")))
    private void sib$saveBagOnThrow(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, @Local Slot slot, @Local ItemStack dropStack) {

        if (player.getWorld().isClient) {
            return;
        }

        BagContainer dropBagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(dropStack, null);

        if (dropBagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
            bagScreenHandler.getBagInventory().onClose(player);
        }

    }

    @Unique
    private ScreenHandler thisAsScreenHandler() {
        return (ScreenHandler) (Object) this;
    }

}
