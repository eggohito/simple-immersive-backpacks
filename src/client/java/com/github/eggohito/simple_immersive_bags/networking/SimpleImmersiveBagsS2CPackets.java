package com.github.eggohito.simple_immersive_bags.networking;

import com.github.eggohito.simple_immersive_bags.networking.s2c.OpenInventoryS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;

public class SimpleImmersiveBagsS2CPackets {

    public static void registerAll() {

        ClientPlayConnectionEvents.INIT.register((handler, client) ->
            ClientPlayNetworking.registerReceiver(OpenInventoryS2CPacket.TYPE, SimpleImmersiveBagsS2CPackets::onInventoryOpened)
        );

    }

    private static void onInventoryOpened(OpenInventoryS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient.getInstance().setScreen(new InventoryScreen(player));
    }

}
