package com.github.eggohito.simple_immersive_bags.mixin;

import com.github.eggohito.simple_immersive_bags.duck.EntityBagUpdateStatus;
import com.github.eggohito.simple_immersive_bags.util.BagUpdateStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityBagUpdateStatus {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private BagUpdateStatus sib$updateStatus = BagUpdateStatus.INIT;

    @Override
    public BagUpdateStatus sib$getStatus() {
        return sib$updateStatus;
    }

    @Override
    public void sib$setStatus(BagUpdateStatus sib$updateStatus) {
        this.sib$updateStatus = sib$updateStatus;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void sib$setStatusToNormal(CallbackInfo ci) {
        this.sib$setStatus(BagUpdateStatus.NONE);
    }

}
