package com.github.eggohito.simple_immersive_bags.mixin;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.inventory.BagInventory;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.util.BagUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ClickType;
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
    private void sib$closeBagInventory(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {

        if (player.getWorld().isClient) {
            return;
        }

        boolean result = switch (actionType) {
            case QUICK_MOVE -> {

                if (!BagUtil.withinEquipmentBounds(slotIndex)) {
                    yield false;
                }

                ItemStack stackInSlot = this.getSlot(slotIndex).getStack();
                BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(stackInSlot, null);

                if (bagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                    bagScreenHandler.getBagInventory().onClose(player);
                    yield true;
                }

                yield false;

            }
            case PICKUP -> {

                if (!this.isValid(slotIndex) || !BagUtil.withinEquipmentBounds(slotIndex)) {
                    yield false;
                }

                ItemStack stackInSlot = this.getSlot(slotIndex).getStack();
                if (ItemStack.areEqual(stackInSlot, this.getCursorStack())) {
                    yield false;
                }

                BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(stackInSlot, null);
                if (bagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {

                    BagInventory bagInventory = bagScreenHandler.getBagInventory();

                    if (button == 0) {
                        bagInventory.onClose(player);
                    }

                    else if (bagInventory.isDirty() && bagInventory.saveable()) {
                        bagInventory.save();
                    }

                    yield true;

                }

                yield false;

            }
            case SWAP -> {

                if (!(button >= 0 && button < 9 || button == 40) || !BagUtil.withinEquipmentBounds(slotIndex)) {
                    yield false;
                }

                Slot slot = this.getSlot(slotIndex);

                ItemStack stackInSwapSlot = player.getInventory().getStack(button);
                ItemStack stackInSlot = slot.getStack();

                if (ItemStack.areEqual(stackInSwapSlot, stackInSlot)) {
                    yield false;
                }

                BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(stackInSlot, null);
                if (stackInSwapSlot.isEmpty() && slot.canTakeItems(player)) {

                    if (bagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                        bagScreenHandler.getBagInventory().onClose(player);
                        yield true;
                    }

                }

                else if (slot.canTakeItems(player) && slot.canInsert(stackInSwapSlot)) {

                    if (bagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                        bagScreenHandler.getBagInventory().onClose(player);
                        yield true;
                    }

                }

                yield false;

            }
            case THROW -> {

                if (!this.getCursorStack().isEmpty()) {
                    yield false;
                }

                ItemStack stackInSlot = this.getSlot(slotIndex).getStack();
                BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(stackInSlot, null);

                if (bagContainer != null && thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                    bagScreenHandler.getBagInventory().onClose(player);
                    yield true;
                }

                yield false;

            }
            default ->
                false;
        };

        if (result) {
            ((EntityBagUpdateStatus) player).sib$setStatus(BagUpdateStatus.NONE);
        }

    }

    @Inject(method = "onSlotClick", at = @At("TAIL"))
    private void sib$openOrCloseBag(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {

        if (player.getWorld().isClient) {
            return;
        }

        if (actionType != SlotActionType.PICKUP || button < 0 || button > 1 || !this.isValid(slotIndex)) {
            return;
        }

        ClickType clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;

        ItemStack stackInSlot = this.getSlot(slotIndex).getStack();
        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(stackInSlot, null);

        if (clickType != ClickType.RIGHT || bagContainer == null) {
            return;
        }

        switch (bagContainer.getState(stackInSlot)) {
            case CLOSED ->
                BagItem.closeHandler(player);
            case OPENED -> {

                if (thisAsScreenHandler() instanceof BagScreenHandler bagScreenHandler) {
                    bagScreenHandler.getBagInventory().onClose(player);
                }

                BagItem.openHandler(player, stackInSlot, true);

            }
        }

    }

    @Unique
    private ScreenHandler thisAsScreenHandler() {
        return (ScreenHandler) (Object) this;
    }

}
