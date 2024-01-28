package com.github.eggohito.simple_immersive_bags.mixin;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import com.github.eggohito.simple_immersive_bags.api.BagContainer;
import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.github.eggohito.simple_immersive_bags.util.BagState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public ScreenHandler currentScreenHandler;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void sib$saveBagOnDrop(CallbackInfo ci) {

        if (!(this.currentScreenHandler instanceof BagScreenHandler bagScreenHandler)) {
            return;
        }

        ItemStack bagStack = bagScreenHandler.getSourceStack();
        BagContainer bagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(bagStack, null);

        if (bagContainer != null) {
            bagScreenHandler.getBagInventory().onClose((PlayerEntity) (Object) this);
        }

    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
    private void sib$closeBagOnDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {

        BagContainer dropBagContainer = SimpleImmersiveBags.ITEM_CONTAINER.find(stack, null);

        if (dropBagContainer != null && dropBagContainer.getState(stack) == BagState.OPENED) {
            dropBagContainer.setState(stack, BagState.CLOSED);
        }

    }

}
