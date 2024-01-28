package com.github.eggohito.simple_immersive_bags.mixin;

import com.github.eggohito.simple_immersive_bags.screen.BagScreenHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void sib$saveBagOnDisconnect(CallbackInfo ci) {

        if (this.currentScreenHandler instanceof BagScreenHandler bagScreenHandler) {
            bagScreenHandler.getBagInventory().onClose(this);
        }

    }

}
