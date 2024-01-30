package com.github.eggohito.simple_immersive_bags.mixin.client;

import com.github.eggohito.simple_immersive_bags.networking.c2s.OpenBagC2SPacket;
import com.github.eggohito.simple_immersive_bags.util.BagUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow @Nullable public ClientPlayerEntity player;

	@WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onInventoryOpened()V")))
	private void sib$overrideInventoryScreen(MinecraftClient instance, Screen screen, Operation<Void> original) {

		BagUtil.getFirstOpenedBag(player).ifPresentOrElse(
			slot -> ClientPlayNetworking.send(new OpenBagC2SPacket(slot)),
			() -> original.call(instance, screen)
		);

	}

}