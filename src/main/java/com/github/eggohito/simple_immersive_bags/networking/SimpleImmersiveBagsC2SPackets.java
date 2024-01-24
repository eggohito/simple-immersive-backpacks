package com.github.eggohito.simple_immersive_bags.networking;

import com.github.eggohito.simple_immersive_bags.content.item.BagItem;
import com.github.eggohito.simple_immersive_bags.networking.c2s.OpenBagC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class SimpleImmersiveBagsC2SPackets {

    public static void registerAll() {

        ServerPlayConnectionEvents.INIT.register((handler, server) ->
            ServerPlayNetworking.registerReceiver(handler, OpenBagC2SPacket.TYPE, SimpleImmersiveBagsC2SPackets::onBagOpened)
        );

    }

    private static void onBagOpened(OpenBagC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {

        ItemStack equippedStack = player.getEquippedStack(packet.slotWithBag());

        if (equippedStack.getItem() instanceof BagItem bagItem) {
            player.openHandledScreen(bagItem);
        }

    }

}
