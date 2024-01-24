package com.github.eggohito.simple_immersive_bags.mixin.client;

import com.github.eggohito.simple_immersive_bags.client.screen.BagScreen;
import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import com.github.eggohito.simple_immersive_bags.networking.c2s.OpenBagC2SPacket;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow @Nullable public ClientPlayerEntity player;

	@Shadow @Nullable public Screen currentScreen;

	@WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onInventoryOpened()V")))
	private void sib$overrideInventoryScreen(MinecraftClient instance, Screen screen, Operation<Void> original) {

		Optional<EquipmentSlot> slotWithBag;
		if (this.player != null && !(this.currentScreen instanceof BagScreen) && (slotWithBag = BagItem.getSlotWithBag(player)).isPresent()) {
			ClientPlayNetworking.send(new OpenBagC2SPacket(slotWithBag.get()));
		}

		else {
			original.call(instance, screen);
		}

	}

}