package com.github.eggohito.simple_immersive_bags.networking.s2c;

import com.github.eggohito.simple_immersive_bags.SimpleImmersiveBags;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record OpenInventoryS2CPacket() implements FabricPacket {

    public static final PacketType<OpenInventoryS2CPacket> TYPE = PacketType.create(
        SimpleImmersiveBags.id("s2c/open_inventory"), OpenInventoryS2CPacket::read
    );

    private static OpenInventoryS2CPacket read(PacketByteBuf buf) {
        return new OpenInventoryS2CPacket();
    }

    @Override
    public void write(PacketByteBuf buf) {

    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
